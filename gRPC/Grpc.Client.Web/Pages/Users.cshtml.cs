namespace Grpc.Client.Web.Pages
{
    public class UsersModel : BasePage
    {
        public List<UserDTO> Users;

        public UsersModel(ILogger<UsersModel> logger) : base(logger)
        {
        }

        async Task<List<UserDTO>> GetUsersByStream()
        {
            List<UserDTO> users = new();
            using var clientData = ServiceClient.GetUsersByStream(new Empty());
            while (await clientData.ResponseStream.MoveNext(new CancellationToken()))
            {
                var user = clientData.ResponseStream.Current;
                users.Add(user);
            }
            return users;
        }

        async Task<List<UserDTO>> GetUsers()
        {
            var result = await ServiceClient.GetAllAsync(new Empty());
            return result.Items.ToList();
        }

        public void OnGet()
        {
            Users = GetUsersByStream().Result;
        }
    }
}