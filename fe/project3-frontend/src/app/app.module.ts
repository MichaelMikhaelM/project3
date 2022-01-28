import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { MainComponent } from './pages/main/main.component';
import { CartComponent } from './pages/cart/cart.component';
import { CheckoutComponent } from './pages/checkout/checkout.component';
import { ReceiptComponent } from './pages/receipt/receipt.component';
import { HeaderComponent } from './components/header/header.component';
import { ProductComponent } from './components/product/product.component';
import { ProductPageComponent } from './pages/product-page/product-page.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RegisterComponent } from './pages/register/register.component';
import { CartItemComponent } from './components/cart-item/cart-item.component';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { AdminNewProductComponent } from './pages/admin-new-product/admin-new-product.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MainComponent,
    CartComponent,
    RegisterComponent,
    CheckoutComponent,
    ReceiptComponent,
    HeaderComponent,
    ProductComponent,
    ProductPageComponent,
    RegisterComponent,
    CartItemComponent,
    AdminPageComponent,
    AdminNewProductComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
