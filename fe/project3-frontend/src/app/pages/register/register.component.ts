import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/User';
import { ApiService } from 'src/app/services/api/api.service'

@Component({
	selector: 'app-register',
	templateUrl: './register.component.html',
	styleUrls: ['./register.component.css'],
	host: {
		class: "page flexColumnTop extraLargeGap"
	}
})
export class RegisterComponent implements OnInit {
	public firstNameInput : string = "";
	public lastNameInput : string = "";
	public emailInput : string = "";
	public usernameInput : string = "";
	public passwordInput : string = "";
	
	public errorMessage : string = "";
	
	constructor (private apiService : ApiService) { }
	
	onKeyDown = (event : KeyboardEvent) : void => {
		if (event.key === "Enter") {
			this.register ();
		}
	};
	
	register = () : void => {
		this.apiService.createUser (<User> {
			firstName: this.firstNameInput,
			lastName: this.lastNameInput,
			email: this.emailInput,
			username: this.usernameInput,
			password: this.passwordInput
		}, undefined, (body : any) : void => {
			this.errorMessage = body.message;
		});
	};
	
	ngOnInit () : void {}
}
