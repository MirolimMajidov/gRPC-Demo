using System.ComponentModel.DataAnnotations;

namespace Grpc.Client.Web.Models
{
    public class ModelUser
    {
        public string Id { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public int Age { get; set; } = 18;

        public Gender Gender { get; set; } = Gender.NotSelected;
    }

    public enum Gender
    {
        [Display(Name = "Not selected")]
        NotSelected,

        [Display(Name = "Female")]
        Female,

        [Display(Name = "Male")]
        Male
    }
}
