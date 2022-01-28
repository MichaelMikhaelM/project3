import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/services/api/api.service';
import { DataService } from 'src/app/services/data/data.service';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css'],
	host: {
		class: "page flexColumnTop extraLargeGap",
		"(scroll)": "onScroll ($event);"
	}
})
export class MainComponent implements OnInit {
	productsPerPage : number = 20;
	
	searchQuery : string = "";
	
	currentPage : number = 0;
	
	lastPage : boolean = false;
	
	constructor (public dataService : DataService, private apiService : ApiService) {}
	
	onKeyDown = (event : KeyboardEvent) : void => {
		if (event.key === "Enter") {
			this.search ();
		}
	};
	
	search = () : void => {
		this.currentPage = 0;
		
		this.dataService.products = [];
		
		this.getProducts ();
	};
	
	getProducts = () : void => {
		this.apiService.getProducts (this.searchQuery, this.currentPage, (body : any) : void => {
			if (body.data.length < this.productsPerPage) {
				this.lastPage = true;
			}
			
			this.dataService.products = this.dataService.products.concat (body.data);
		});
	};
	
	onScroll = (event : Event) : void => {
		if (this.lastPage) {
			//don't try to get more products if we have already loaded the last page
			return;
		}
		
		const element : HTMLElement = <HTMLElement> event.target;
		
		//if we are scrolled to the bottom
		if (element.scrollHeight - element.offsetHeight === element.scrollTop) {
			this.currentPage += 1;
			
			this.getProducts ();
		}
	};
	
	ngOnInit () : void {
		this.dataService.products = [];
		
		this.getProducts ();
	}
}
