import { Product } from 'src/app/models/Product';
import { CartItem } from './CartItem';
import { Role } from './Role';

export interface User {
	id : number;
	firstName : string;
	lastName : string;
	username : string;
	password : string;
	email: string;
	role: Role;
	//todo change to object that maps productId to quantity
	cart : CartItem [];
};