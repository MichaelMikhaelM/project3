import { Product } from "./Product";

export interface CartItem{
    id: number;
    //todo change to productId
    product: Product;
    quantity: number;
}