﻿using Grpc.Client.Web;
using Grpc.Client.Web.Models;
using Grpc.Net.Client;
using Microsoft.AspNetCore.Mvc;
using static Grpc.Client.Web.UserService;

namespace Titan.Controllers
{
    public class UserController : Controller
    {
        private readonly ILogger<UserController> _logger;
        private readonly UserServiceClient _serviceClient;

        public UserController(ILogger<UserController> logger)
        {
            _logger = logger;
            var grpcChannel = GrpcChannel.ForAddress("https://localhost:9595/");
            _serviceClient = new UserServiceClient(grpcChannel);
        }

        public IActionResult Index()
        {
            ViewData["Title"] = "All users";
            var users = GetUsersByStream().Result;
            return View(users);
        }

        async Task<List<UserDTO>> GetUsersByStream()
        {
            List<UserDTO> users = new();
            using var clientData = _serviceClient.GetUsersByStream(new Empty());
            while (await clientData.ResponseStream.MoveNext(new CancellationToken()))
            {
                var user = clientData.ResponseStream.Current;
                users.Add(user);
            }
            return users;
        }

        async Task<List<UserDTO>> GetUsers()
        {
            var result = await _serviceClient.GetAllAsync(new Empty());
            return result.Items.ToList();
        }

        public async Task<IActionResult> Details(string id)
        {
            var user = await _serviceClient.GetByIdAsync(new UserId { Id = id });
            var modelUser = new ModelUser
            {
                Id = user.Id,
                FirstName = user.FirstName,
                LastName = user.LastName,
                Age = user.Age,
                Gender = user.Gender == 1 ? Gender.Male : Gender.Female
            };
            ViewData["Title"] = "User info";
            return View(modelUser);
        }

        public IActionResult Create()
        {
            var newUser = new UserDTO() { Age = 18, Gender = (int)Gender.Male };
            ViewData["Title"] = "Create";
            return View(newUser);
        }

        [HttpPost]
        public async Task<IActionResult> Create(UserDTO user)
        {
            if (!ModelState.IsValid)
            {
                ModelState.AddModelError("", "Enter all information!");
                return View();
            }
            else
            {
                var result = await _serviceClient.CreateAsync(user);
                if (string.IsNullOrEmpty(result.Id))
                {
                    ModelState.AddModelError("", "Could not save new user!");
                    return View();
                }
                else
                {
                    return RedirectToAction(nameof(Index), new { id = result.Id });
                }
            }
        }

        public async Task<IActionResult> Edit(string id)
        {
            var user = await _serviceClient.GetByIdAsync(new UserId { Id = id });
            ViewData["Title"] = "Edit user info";
            return View(user);
        }

        [HttpPost]
        public async Task<IActionResult> Edit(UserDTO user)
        {
            if (!ModelState.IsValid)
            {
                ModelState.AddModelError("", "Enter all information!");
                return View();
            }
            else
            {
                var result = await _serviceClient.UpdateAsync(user);
                if (result.Successful)
                {
                    return RedirectToAction(nameof(Index), new { id = user.Id });
                }
                else
                {
                    ModelState.AddModelError("", "Could not save a new user!");
                    return View();
                }
            }
        }


        public async Task<IActionResult> Delete(string id)
        {
            var user = await _serviceClient.GetByIdAsync(new UserId { Id = id });
            ViewData["Title"] = "Delete user";
            return View(user);
        }

        [HttpPost]
        public async Task<IActionResult> Delete(UserId userId)
        {
            var result = await _serviceClient.DeleteAsync(new UserId { Id = userId.Id });
            if (result.Successful)
            {
                return RedirectToAction(nameof(Index));
            }
            else
            {
                ModelState.AddModelError("", "Could not delete the user!");
                return View();
            }
        }
    }
}
