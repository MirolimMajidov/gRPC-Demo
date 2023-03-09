using Grpc.Core;
using Grpc.Service.Models;
using Grpc.Service.Protos;
using Microsoft.EntityFrameworkCore;

namespace Grpc.Service.Services
{
    public class ProductService : Protos.ProductService.ProductServiceBase
    {
        public override async Task<Products> GetAllProducts(EmptyProduct request, ServerCallContext context)
        {
            Products products = new Products();
            //Your logic
            return await Task.FromResult(products);
        }

        public override async Task<Product> GetProductById(ProductId request, ServerCallContext context)
        {
            //Your logic
            return await Task.FromResult(new Product());
        }

        public override async Task<ProductId> Create(Product request, ServerCallContext context)
        {
            //Your logic
            return await Task.FromResult(new ProductId { Id = request.Id });
        }
    }
}