import { Component, Input, OnInit } from '@angular/core';
import { CartItem } from 'src/app/models/CartItem';
import { ApiService } from 'src/app/services/api/api.service';
import { DataService } from 'src/app/services/data/data.service';

@Component({
	selector: 'app-cart-item',
	templateUrl: './cart-item.component.html',
	styleUrls: ['./cart-item.component.css'],
	host: {
		style: "width: 100%;"
	}
})
export class CartItemComponent implements OnInit {
	@Input ()
	public cartItem : CartItem = <CartItem> {};

	constructor (private apiService : ApiService, private dataService : DataService) {}
	
	onQuantityInput = (event : any) : void => {
		//todo allow backspacing but when unfocus set to 1 if still blank
		//todo quantityInput should be updated automatically
		
		event.target.value = Math.min (this.cartItem.product.stock, Math.max (1, event.target.value));
		
		this.cartItem.quantity = event.target.value;
		
		this.apiService.updateCartItem (this.cartItem.id, this.cartItem.quantity);
	}
	
	removeFromCart = () : void => {
		this.apiService.deleteCartItem (this.cartItem.id, (body : any) : void => {
			this.dataService.user.cart.splice (this.dataService.user.cart.indexOf (this.cartItem), 1);
			
			this.dataService.updateUser (this.dataService.user);
		});
	};
	
	ngOnInit () : void {
		
	}
}
