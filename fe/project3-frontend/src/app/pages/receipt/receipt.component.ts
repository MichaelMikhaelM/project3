import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Transaction } from 'src/app/models/Transaction';
import { ApiService } from 'src/app/services/api/api.service';
import { DataService } from 'src/app/services/data/data.service';

@Component({
	selector: 'app-receipt',
	templateUrl: './receipt.component.html',
	styleUrls: ['./receipt.component.css'],
	host: {
		class: "page flexColumnTop extraLargeGap"
	}
})
export class ReceiptComponent implements OnInit {
	public transaction : Transaction = <Transaction> {};
	
	constructor (private activatedRoute : ActivatedRoute, private apiService : ApiService, private dataService : DataService, private router : Router) {}
	
	ngOnInit () : void {
		if (this.dataService.user.username === undefined) {
			this.router.navigate (["/"]);
			
			return;
		}
		
		this.activatedRoute.params.subscribe (paramaters => {
			this.apiService.getTransaction (parseInt (paramaters ["transactionId"]), (body : any) : void => {
				body.data.items = JSON.parse (body.data.items);
				
				this.transaction = body.data;
			});
		});
	}
}
