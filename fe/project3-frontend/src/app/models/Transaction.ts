import { CartItem } from "./CartItem";
import { User } from "./User";

export interface Transaction {
	id : number;
	buyer : User;
	items : CartItem [];
	total : number;
};