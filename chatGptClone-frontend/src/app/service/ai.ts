import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class Ai {
  backendHost: string="http://localhost:8080"

  constructor(private http: HttpClient) {
  }

  public getResponse(querry: string){
    return  this.http.get(this.backendHost+"/stream?query="+querry)
  }
}
