import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { AdminNewProductComponent } from "./pages/admin-new-product/admin-new-product.component";
import { AdminPageComponent } from "./pages/admin-page/admin-page.component";
import { CartComponent } from "./pages/cart/cart.component";
import { CheckoutComponent } from "./pages/checkout/checkout.component";
import { LoginComponent } from "./pages/login/login.component";
import { MainComponent } from "./pages/main/main.component";
import { ProductPageComponent } from "./pages/product-page/product-page.component";
import { ReceiptComponent } from "./pages/receipt/receipt.component";
import { RegisterComponent } from "./pages/register/register.component";

const routes: Routes = [
	{
		path: "login",
		component: LoginComponent
	},
	
	{
		path: "admin/:id",
		component: AdminPageComponent
	},
	
	{
		path: "admin-new-product",
		component: AdminNewProductComponent
	},
	
	{
		path: "product/:productId",
		component: ProductPageComponent
	},
	
	{
		path: "cart",
		component: CartComponent
	},
	
	{
		path: "checkout",
		component: CheckoutComponent
	},
	
	{
		path: "register",
		component: RegisterComponent
	},
	
	{
		path: "receipt/:transactionId",
		component: ReceiptComponent
	},
	
	{
		path: "**",
		component: MainComponent
	}
];

@NgModule ({
	imports: [RouterModule.forRoot (routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }