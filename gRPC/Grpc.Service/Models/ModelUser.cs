﻿using Grpc.Service.Protos;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Grpc.Service.Models
{
    public class ModelUser
    {
        [Key, DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public Guid Id { get; set; } = Guid.NewGuid();

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public int Age { get; set; } = 18;

        public Gender Gender { get; set; } = Gender.NotSelected;

        public UserDTO ConvertToDTO()
        {
            return new UserDTO
            {
                Id = Id.ToString()


            };
        }
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