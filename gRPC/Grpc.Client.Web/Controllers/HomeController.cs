using Microsoft.AspNetCore.Mvc;

namespace Titan.Controllers
{
    public class HomeController : Controller
    {
        private ILogger _logger;

        public HomeController(ILogger<HomeController> logger)
        {
            _logger = logger;
        }

        public IActionResult Index()
        {
            ViewData["Title"] = "Home page";
            return View();
        }
    }
}
