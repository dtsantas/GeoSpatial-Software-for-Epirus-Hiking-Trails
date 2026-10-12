import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UpdateTrailPayload {
  name: string;
  location: string;
  duration: number;
  difficulty: string;
  description: string;
}

@Injectable({
  providedIn: 'root',
})
export class TrailService {
    private apiUrl = environment.apiUrl + '/';
    constructor(private http: HttpClient){}

    getTrails(): Observable<any[]>{
        return this.http.get<any[]>(this.apiUrl + "all_trails")
    }

    getUserTrails(): Observable<any[]>{
        return this.http.get<any[]>(this.apiUrl + "user_trails")
    }

    createTrail(trail: FormData): Observable<any> {
      return this.http.post(this.apiUrl + "createTrail", trail)
    }

    deleteTrail(id: number): Observable<Object>{
        return this.http.delete(this.apiUrl+ "deleteTrail/"+ id);
    }
    
    addImage(image: String): Observable<any> {
        return this.http.post(this.apiUrl + "addTrailImage/", image)
    }

    getTrail(id: number): Observable<any>{
        return this.http.get<any>(this.apiUrl + "trail_by_id/"+ id)
    }

    addImages(trailId: number, images: File[]): Observable<any> {
        const formData = new FormData();
        images.forEach(img => formData.append('images', img));
        return this.http.post(this.apiUrl + "add_images/" + trailId, formData)
    }


    getImagesByTrail(trailId: number): Observable<any[]>{
        return this.http.get<any[]>(this.apiUrl + "images_by_trail/" + trailId)
    }
    deleteImage(trailId: number): Observable<any>{
        return this.http.delete(this.apiUrl + "deleteImage/" + trailId)
    }


    getTrailPoints(trailId: number): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl + `api/trails/${trailId}/points`);
    }

    saveTrailPoints(trailId: number, points: { lat: number; lng: number; elevation?: number }[]): Observable<any[]> {
    return this.http.post<any[]>(this.apiUrl + `api/trails/${trailId}/points`, points);
    }

    importGpx(trailId: number, file: File): Observable<any[]> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any[]>(this.apiUrl + `api/trails/${trailId}/points/gpx`, formData);
    }

    getGpxFile(trailId: number): Observable<Blob> {
    return this.http.get(
        this.apiUrl + `api/trails/${trailId}/points/gpx`,
        { responseType: 'blob' }
    );
    }

    updateTrail(id: number, data: UpdateTrailPayload) {
  const token = localStorage.getItem('token');

  return this.http.put<{ message: string }>(
    this.apiUrl + `updateTrail/${id}`,
    data,
    {
      headers: token
        ? { Authorization: `Bearer ${token}` }
        : {}
    }
  );
}
}