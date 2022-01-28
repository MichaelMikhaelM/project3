import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from 'src/app/services/api/api.service';
import { DataService } from 'src/app/services/data/data.service';

@Component({
	selector: 'app-cart',
	templateUrl: './cart.component.html',
	styleUrls: ['./cart.component.css'],
	host: {
		class: "page flexColumnTop extraLargeGap"
	}
})
export class CartComponent implements OnInit {
	constructor (public dataService : DataService, private apiService : ApiService, private router : Router) {}
	
	ngDoCheck () : void {
		this.dataService.updateCartTotals ();
	}
	
	ngOnInit () : void {
		if (this.dataService.user.username === undefined) {
			this.router.navigate (["/"]);
			
			return;
		}
		
		this.apiService.getCartItems ((body : any) : void => {
			this.dataService.user.cart = body.data;
			
			this.dataService.updateCartTotals ();
			
			//todo refactor
			this.dataService.updateUser (this.dataService.user);
		});
	}
}
