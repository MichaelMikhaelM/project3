import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Form } from "@angular/forms";
import { Router } from "@angular/router";
import { Observable } from "rxjs";
import { Product } from "src/app/models/Product";
import { User } from "src/app/models/User";
import { environment } from "src/environments/environment";
import { DataService } from "../data/data.service";

@Injectable ({
	providedIn: "root"
})
export class ApiService {
	constructor (private httpClient : HttpClient, private router : Router, public dataService: DataService) {}
	
	handleResponse = async (response : Observable <any>, callback? : Function, errorCallback? : Function) : Promise <any> => {
		const handler = async (body : any) : Promise <any> => {
			//todo remove, for debugging/presentation
			console.log (body);
			
			if (body.success) {
				if (typeof callback !== "undefined") {
					await callback (body);
				}
			}
			
			else {
				if (typeof errorCallback !== "undefined") {
					errorCallback (body);
				}
				
				else {
					alert (body.message);
				}
				
				// Session is stale or server restarted, delete user information from data service and local storage
				// This is a 401 error created by throwing "UnauthorizedException" on backend which will redirect frontend to login page
				if (body.message=="Error! Unauthorized") {
					this.dataService.updateUser (<User> {});
				}
			}
			
			if (body.redirect !== undefined && body.redirect !== null) {
				this.router.navigate ([body.redirect]);
			}
		};
		
		//send body from response to handler (HttpErrorResponses keep body in error.error)
		response.subscribe ({
			next: handler,
			
			error: (error : HttpErrorResponse) : void => {
				handler (error.error);
			}
		});
	};
	
	get = (path : string) : Observable <any> => {
		return this.httpClient.get (environment.apiBaseUrl + path, {
			withCredentials: true
		});
	};
	
	post = (path : string, body : any) : Observable <any> => {
		return this.httpClient.post (environment.apiBaseUrl + path, body, {
			withCredentials: true
		});
	};
	
	put = (path : string, body : any) : Observable <any> => {
		return this.httpClient.put (environment.apiBaseUrl + path, body, {
			withCredentials: true
		});
	};
	
	patch = (path : string, body : any) : Observable <any> => {
		return this.httpClient.patch (environment.apiBaseUrl + path, body, {
			withCredentials: true
		});
	};
	
	delete = (path : string) : Observable <any> => {
		return this.httpClient.delete (environment.apiBaseUrl + path, {
			withCredentials: true
		});
	};
	
	//session
	
	createSession = (identifier : string, password : string, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.post ("session", {
			identifier: identifier,
			password: password
		}), callback, errorCallback);
	}
	
	deleteSession = (callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.delete ("session"), callback, errorCallback);
	}
	
	//product
	
	getProducts = (searchQuery : string, page : number, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.get ("product?searchQuery=" + searchQuery + "&page=" + page), callback, errorCallback);
	}
	
	getProduct = (id : number, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.get ("product/" + id), callback, errorCallback);
	};
	
	updateProductItem (formData : FormData, callback? : Function, errorCallback? : Function) {
		this.handleResponse (this.patch ("product", formData), callback, errorCallback);
	}
	
	//Admin team addition
	createNewProduct (formData : FormData, callback? : Function, errorCallback? : Function) {
		this.handleResponse (this.post ("product", formData), callback, errorCallback);
	}
	
	//user
	
	createUser = (user: User, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.post ("user", {
			firstName: user.firstName,
			lastName: user.lastName,
			username: user.username,
			email: user.email,
			password: user.password
		}), callback, errorCallback);
	};
	
	//cartitem
	
	createCartItem = (productId : number, quantity : number, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.post ("cartitem", {
			productId: productId,
			quantity: quantity
		}), callback, errorCallback);
	};
	
	getCartItems = (callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.get ("cartitem"), callback, errorCallback);
	};
	
	updateCartItem = (id : number, quantity : number, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.put ("cartitem/" + id, {
			quantity: quantity
		}), callback, errorCallback);
	};
	
	deleteCartItem = (id : number, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.delete ("cartitem/" + id), callback, errorCallback);
	};
	
	//transaction
	
	createTransaction = (callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.post ("transaction", {}), callback, errorCallback);
	};
	
	getTransaction = (id : number, callback? : Function, errorCallback? : Function) : void => {
		this.handleResponse (this.get ("transaction/" + id), callback, errorCallback);
	};
}