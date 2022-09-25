using System.ComponentModel.DataAnnotations;

namespace Grpc.Client.Web.Models
{
    public class ModelUser
    {
        public string Id { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public int Age { get; set; } = 18;

        public Gender Gender { get; set; } = Gender.Male;
    }

    public enum Gender
    {
        [Display(Name = "Female")]
        Female,

        [Display(Name = "Male")]
        Male
    }
}
