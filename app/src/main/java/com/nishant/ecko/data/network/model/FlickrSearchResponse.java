package com.nishant.ecko.data.network.model;

import java.util.ArrayList;

public class FlickrSearchResponse {
    private Photos photos;

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    public FlickrSearchResponse(Photos photos) {

        this.photos = photos;
    }

    public class Photos {
        private long page;
        private long pages;
        private long perpage;
        private long total;
        private ArrayList<Photo> photo;

        public long getPage() {
            return page;
        }

        public void setPage(long page) {
            this.page = page;
        }

        public long getPages() {
            return pages;
        }

        public void setPages(long pages) {
            this.pages = pages;
        }

        public long getPerpage() {
            return perpage;
        }

        public void setPerpage(long perpage) {
            this.perpage = perpage;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public ArrayList<Photo> getPhoto() {
            return photo;
        }

        public void setPhoto(ArrayList<Photo> photo) {
            this.photo = photo;
        }

        public Photos(long page, long pages, long perpage, long total, ArrayList<Photo> photo) {

            this.page = page;
            this.pages = pages;
            this.perpage = perpage;
            this.total = total;
            this.photo = photo;
        }

        public class Photo {
            private long id;
            private String secret;
            private String title;
            private String server;

            public String getServer() {
                return server;
            }

            public void setServer(String server) {
                this.server = server;
            }

            public Photo(long id, String secret, String title, String server) {

                this.id = id;
                this.secret = secret;
                this.title = title;
                this.server = server;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

        }
    }
}
