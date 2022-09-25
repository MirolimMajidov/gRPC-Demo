using Microsoft.EntityFrameworkCore;

namespace Grpc.Service.Models
{
    public class UserContext : DbContext
    {
        public UserContext(DbContextOptions<UserContext> options)
            : base(options)
        {
        }

        public DbSet<ModelUser> Users { get; set; }
    }
}
