using Grpc.Core;
using Grpc.Service.Models;
using Grpc.Service.Protos;
using Microsoft.EntityFrameworkCore;

namespace Grpc.Service.Services
{
    public class UsersService : UserService.UserServiceBase
    {
        private readonly ILogger<UsersService> _logger;
        private readonly UserContext _context;

        public UsersService(ILogger<UsersService> logger, UserContext context)
        {
            _logger = logger;
            _context = context;
        }

        #region GetAll

        public override async Task<Users> GetAll(Empty request, ServerCallContext context)
        {
            _logger.LogInformation("GetAll API is calling...");

            Users users = new Users();
            users.Items.AddRange(_context.Users.Select(u => u.ConvertToDTO()));
            return await Task.FromResult(users);
        }

        #endregion

        #region GetUsersByStream

        public override async Task GetUsersByStream(Empty request, IServerStreamWriter<UserDTO> response, ServerCallContext context)
        {
            _logger.LogInformation("GetUsersByStream API is calling...");

            foreach (var user in _context.Users.OrderBy(u => u.FirstName))
                await response.WriteAsync(user.ConvertToDTO());
        }

        #endregion

        #region GetById

        public override async Task<UserDTO> GetById(UserId request, ServerCallContext context)
        {
            _logger.LogInformation("GetById API is calling...");
            if (Guid.TryParse(request.Id, out Guid userId))
            {
                var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == userId);
                if (user == null)
                {
                    var message = $"User with {userId} ID does not exist";
                    context.Status = new Status(StatusCode.NotFound, message);
                    _logger.LogError(message);

                    return null;
                }

                return await Task.FromResult(user.ConvertToDTO());
            }
            else
            {
                var message = $"Id must be Guid type (received '{request.Id}')";
                context.Status = new Status(StatusCode.InvalidArgument, message);
                _logger.LogError(message);
                return null;
            }
        }

        #endregion

        #region Create

        public override async Task<UserId> Create(UserDTO request, ServerCallContext context)
        {
            _logger.LogInformation("Create API is calling...");

            if (string.IsNullOrEmpty(request.FirstName) || string.IsNullOrEmpty(request.LastName))
            {
                var message = $"The first and last name of user cannot be empty";
                context.Status = new Status(StatusCode.InvalidArgument, message);
                _logger.LogError(message);

                return null;
            }
            else
            {
                var user = new ModelUser()
                {
                    FirstName = request.FirstName,
                    LastName = request.LastName,
                    Age = request.Age,
                    Gender = (Gender)request.Gender
                };
                _context.Add(user);
                await _context.SaveChangesAsync();
                return new UserId { Id = user.Id.ToString() };
            }
        }

        #endregion

        #region Update

        public override async Task<Result> Update(UserDTO request, ServerCallContext context)
        {
            _logger.LogInformation("Update API is calling...");

            if (string.IsNullOrEmpty(request.FirstName) || string.IsNullOrEmpty(request.LastName) || !Guid.TryParse(request.Id, out Guid userId) || userId == Guid.Empty)
            {
                var message = $"The first and last name and Id of user cannot be empty";
                context.Status = new Status(StatusCode.InvalidArgument, message);
                _logger.LogError(message);

                return null;
            }
            else
            {
                var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == userId);
                if (user == null)
                {
                    var message = $"User with {userId} ID does not exist";
                    context.Status = new Status(StatusCode.NotFound, message);
                    _logger.LogError(message);

                    return null;
                }

                user.FirstName = request.FirstName;
                user.LastName = request.LastName;
                user.Age = request.Age;
                user.Gender = (Gender)request.Gender;

                _context.Update(user);
                await _context.SaveChangesAsync();
                return new Result { Successful = true };
            }
        }

        #endregion

        #region Delete

        public override async Task<Result> Delete(UserId request, ServerCallContext context)
        {
            _logger.LogInformation("Delete API is calling...");
            if (Guid.TryParse(request.Id, out Guid userId))
            {
                var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == userId);
                if (user == null)
                {
                    var message = $"User with {userId} ID does not exist";
                    context.Status = new Status(StatusCode.NotFound, message);
                    _logger.LogError(message);

                    return null;
                }
                _context.Users.Remove(user);
                await _context.SaveChangesAsync();

                return new Result { Successful = true };
            }
            else
            {
                var message = $"Id must be Guid type (received '{request.Id}')";
                context.Status = new Status(StatusCode.InvalidArgument, message);
                _logger.LogError(message);
                return null;
            }
        }

        #endregion
    }
}