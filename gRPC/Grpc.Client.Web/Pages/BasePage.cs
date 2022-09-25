using Grpc.Net.Client;
using Microsoft.AspNetCore.Mvc.RazorPages;
using static Grpc.Client.Web.UserService;

namespace Grpc.Client.Web.Pages
{
    public abstract class BasePage : PageModel
    {
        public readonly ILogger<BasePage> Logger;
        public readonly UserServiceClient ServiceClient;

        public BasePage(ILogger<BasePage> logger)
        {
            Logger = logger;
            var grpcChannel = GrpcChannel.ForAddress("https://localhost:5001/");
            ServiceClient = new UserServiceClient(grpcChannel);
        }
    }
}
