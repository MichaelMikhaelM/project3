import { Injectable } from '@angular/core';
import { CartItem } from 'src/app/models/CartItem';
import { Product } from 'src/app/models/Product';
import { User } from 'src/app/models/User';

@Injectable({
	providedIn: 'root'
})
export class DataService {
	public products : Product [] = [];
	
	public user : User = <User> {};
	
	public cartSubtotal : number = 0;
	public cartSales : number = 0;
	public cartTotal : number = 0;
	
	constructor () {
		if (localStorage ["user"] !== undefined) {
			this.user = JSON.parse (localStorage ["user"]);
		}
	}
	
	updateUser = (user : User) : void => {
		this.user = user;
		
		localStorage ["user"] = JSON.stringify (user);
	};
	
	updateCartTotals = () : void => {
		this.cartSubtotal = 0;
		this.cartSales = 0;
		this.cartTotal = 0;
		
		for (let i = 0; i < this.user.cart.length; i++) {
			if (this.user.cart [i].product.salePrice) {
				this.cartSales += (this.user.cart [i].product.price - this.user.cart [i].product.salePrice) * this.user.cart [i].quantity;
			}
			
			this.cartSubtotal += this.user.cart [i].product.price * this.user.cart [i].quantity;
			
			this.cartTotal += (this.user.cart [i].product.salePrice || this.user.cart [i].product.price) * this.user.cart [i].quantity;
		}
	}; 
}