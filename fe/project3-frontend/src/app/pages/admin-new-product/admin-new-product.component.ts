import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from 'src/app/models/Product';
import { ApiService } from 'src/app/services/api/api.service';
import { DataService } from 'src/app/services/data/data.service';

@Component({
	selector: 'app-admin-new-product',
	templateUrl: './admin-new-product.component.html',
	styleUrls: ['./admin-new-product.component.css']
})
export class AdminNewProductComponent implements OnInit {

	newProduct: Product = <Product>{};
	product: Product = <Product>{};
	id: number = 0;
	imgInput: FileList = <FileList>{};

	errMessage: string = '';

	constructor(private apiService: ApiService, private dataService: DataService, private activatedRoute: ActivatedRoute, private router: Router) { 

	}

	ngOnInit(): void {
		if(this.dataService.user?.role?.role != "ADMIN") {
			this.router.navigate(["/"]);
		}
	}

	fileInput(event: any){
		this.imgInput = event.target.files;
	}

	createProduct(){
		var formData: FormData = new FormData();

		let file: File = this.imgInput[0];


		if(this.newProduct.salePrice){
			this.newProduct.salePrice = +this.newProduct.salePrice.toFixed(2);
			console.log(this.newProduct.salePrice);
		}
			
		
		
		if(this.newProduct.price){
			this.newProduct.price = +this.newProduct.price.toFixed(2);
			console.log(this.newProduct.price);
		}else{
			this.newProduct.price = 0.93;
		}
	
		formData.append("name", this.newProduct.name)
		formData.append("description", this.newProduct.description);
		formData.append("price", JSON.stringify(this.newProduct.price));

		if(this.newProduct.salePrice){
			formData.append("salePrice", JSON.stringify(this.newProduct.salePrice));
		}

		if(this.newProduct.stock){
			formData.append("stock", JSON.stringify(this.newProduct.stock));
		}
		formData.append("imageUrl", this.newProduct.imageUrl);
		formData.append("file", file);


		console.log("new product");
		console.log(this.newProduct);

		console.log("product");
		console.log(this.product);
		if(this.newProduct.price < this.newProduct.salePrice){
			console.log("price error hit.")
			this.errMessage = "Sale price cannot be higher than price.";
		}

		if((this.newProduct.price < 0) || (this.newProduct.salePrice < 0) || (this.newProduct.stock < 0)) {
			this.errMessage = "Values cannot be less than 0.";
		} else {
			this.apiService.createNewProduct (formData);
		}
	}

}
