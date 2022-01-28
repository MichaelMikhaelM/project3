import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from 'src/app/services/api/api.service'
import { DataService } from 'src/app/services/data/data.service';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css'],
	host: {
		class: "page flexColumnTop extraLargeGap"
	}
})
export class LoginComponent implements OnInit {
	public identifierInput : string = "";
	public passwordInput : string = "";
	
	public errorMessage : string = "";

	constructor (private apiServ: ApiService, private dataService: DataService) {}
	
	onKeyDown = (event : KeyboardEvent) : void => {
		if (event.key === "Enter") {
			this.login ();
		}
	};
	
	login = () : void => {
		this.apiServ.createSession (this.identifierInput, this.passwordInput, (body : any) : void => {
			this.dataService.updateUser (body.data);
		}, (body : any) => {
			this.errorMessage = body.message;
		});
	};
	
	ngOnInit () {}
}
