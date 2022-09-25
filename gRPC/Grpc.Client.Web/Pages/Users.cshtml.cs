using Grpc.Net.Client;
using Microsoft.AspNetCore.Mvc.RazorPages;
using static Grpc.Client.Web.UserService;

namespace Grpc.Client.Web.Pages
{
    public class UsersModel : PageModel
    {
        private readonly ILogger<UsersModel> _logger;
        private readonly List<UserDTO> _users;

        public UsersModel(ILogger<UsersModel> logger)
        {
            _logger = logger;

            var grpcChannel = GrpcChannel.ForAddress("http://localhost:9595/");
            var client = new UserServiceClient(grpcChannel);
            _users = GetUsersByStream(client).Result;
        }

        async Task<List<UserDTO>> GetUsersByStream(UserServiceClient client)
        {
            List<UserDTO> users = new();
            using var clientData = client.GetUsersByStream(new Empty());
            while (await clientData.ResponseStream.MoveNext(new CancellationToken()))
            {
                var user = clientData.ResponseStream.Current;
                users.Add(user);
            }
            return users;
        }

        async Task<List<UserDTO>> GetUsers(UserServiceClient client)
        {
            var result = await client.GetAllAsync(new Empty());
            return result.Items.ToList();
        }

        public void OnGet()
        {
        }
    }
}