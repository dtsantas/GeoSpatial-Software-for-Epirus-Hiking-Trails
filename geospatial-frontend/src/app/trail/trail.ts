import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { UserServices } from '../services/user.services';
import { TrailService } from '../services/trail.service';
import { Trail } from '../models/trail.model';
import { DataViewModule } from 'primeng/dataview';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { FileUploadModule } from 'primeng/fileupload';
import { CarouselModule } from 'primeng/carousel';
import { ConfirmationService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TrailMapComponent } from '../trail-map-editor/trail-map-editor';
import { GalleriaModule } from 'primeng/galleria';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { UpdateTrailPayload } from '../services/trail.service';



@Component({
  selector: 'app-trail',
  standalone: true,
  imports: [CommonModule,CarouselModule, ConfirmDialogModule, 
    ProgressSpinnerModule, FileUploadModule, RouterModule, CommonModule, 
    RouterModule, DataViewModule, ButtonModule, CardModule, TrailMapComponent, GalleriaModule,
    FormsModule, DialogModule],
    providers: [ConfirmationService],
  templateUrl: './trail.html',
  styleUrl: './trail.css',
})
export class TrailPage implements OnInit {

  selectedImages: File[] = [];

 onImagesSelect(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files) {
    this.selectedImages = Array.from(input.files);
    console.log('Selected:', this.selectedImages.length, 'files');
  }
}

  trailId!: number;
  trail!: Trail;
  images: { id: number, imageURL: string }[] = [];

  constructor(
    public userService: UserServices,
    private trailService: TrailService,
    private router: Router,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService
  ) {}

ngOnInit(): void {
  const id = this.route.snapshot.paramMap.get('id');
  if (id) {
    this.trailId = Number(id);  
    this.getTrail(this.trailId);
    this.getImages(this.trailId);
  }
}
getImages(trailId: number) {
  this.trailService.getImagesByTrail(trailId).subscribe({
    next: (data) => {
      this.images = data;
    },
    error: (err) => {
      console.error('Failed to load images', err);
    }
  });
}

getTrail(id: number) {
  this.trailService.getTrail(id).subscribe({
    next: (data) => {
      this.trail = data;
    },
    error: (err) => {
      console.error('Failed to load trail', err);
    }
  });
}

  deleteTrail(id: number) {
  this.trailService.deleteTrail(id).subscribe({
    next: () => {
      this.router.navigate(['trail-list']);
    },
    error: (err) => {
      console.error('Delete failed', err);
    }
  });
}

  deleteImage(imageId: number): void {
  this.trailService.deleteImage(imageId).subscribe(() => {
    this.images = this.images.filter(img => img.id !== imageId);
    });
  }

  getReviews(id: number){
    this.router.navigate([`review-list/${id}`])
  }

  createReview(id: number){
    this.router.navigate([`create-review/${id}`])
  }

  addImages(trailId: number) {
  if (this.selectedImages.length === 0) return;

  this.trailService.addImages(trailId, this.selectedImages).subscribe({
    next: () => {
      this.selectedImages = [];

      this.getImages(trailId);
    },
    error: (err) => {
      console.error('Upload failed', err);
    }
  });
}
  confirmDeleteTrail(id: number): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this trail?',
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.deleteTrail(id);
      }
    });
  }

  confirmDeleteImage(imageId: number): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this photo?',
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.deleteImage(imageId);
      }
    });
  }
  gpxFileUrl(trailId: number): any {
  this.trailService.getGpxFile;
}

galleryVisible = false;
galleryIndex = 0;

openGallery(img: { imageURL: string }) {
  const index = this.images.findIndex(
    image => image.imageURL === img.imageURL
  );

  this.galleryIndex = index >= 0 ? index : 0;
  this.galleryVisible = true;
}

nextImage() {
  if (this.images.length > 1) {
    this.galleryIndex =
      (this.galleryIndex + 1) % this.images.length;
  }
}

previousImage() {
  if (this.images.length > 1) {
    this.galleryIndex =
      (this.galleryIndex - 1 + this.images.length)
      % this.images.length;
  }
}

editVisible = false;
savingEdit = false;
editError = '';

editData: UpdateTrailPayload = {
  name: '',
  location: '',
  duration: 0,
  difficulty: 'Easy',
  description: ''
};

openEditDialog() {
  if (this.userService.getRole() !== 'ROLE_ADMIN') {
    return;
  }

  this.editData = {
    name: this.trail.trailName,
    location: this.trail.location,
    duration: Number(this.trail.duration),
    difficulty: this.trail.difficulty,
    description: this.trail.description ?? ''
  };

  this.editError = '';
  this.editVisible = true;
}

saveTrail() {
  if (
    this.userService.getRole() !== 'ROLE_ADMIN' ||
    !this.trail.id ||
    this.savingEdit
  ) {
    return;
  }

  this.savingEdit = true;
  this.editError = '';

  this.trailService.updateTrail(
    this.trail.id,
    this.editData
  ).subscribe({
    next: () => {
      this.savingEdit = false;
      this.editVisible = false;
      this.getTrail(this.trailId);
    },
    error: (err) => {
      console.error('Update failed', err);
      this.editError = 'Failed to update trail.';
      this.savingEdit = false;
    }
  });
}

}