package ro.adlabs;

import java.util.ArrayList;

/**
 * Created by danny on 2/23/17.
 */
public class Classes {
    public static class Video implements Comparable<Video> {
        int id;
        int size;

        public int getId() {
            return id;
        }

        public Video setId(int id) {
            this.id = id;
            return this;
        }

        public int getSize() {
            return size;
        }

        public Video setSize(int size) {
            this.size = size;
            return this;
        }

        @Override
        public int compareTo(Video o) {
            return size - o.getSize();
        }
    }

    public static class Cache implements Comparable<Cache> {
        int id;
        int latency;
        int available;
        ArrayList<Video> videos;

        public Cache() {
            videos = new ArrayList<>();
        }

        public int getId() {
            return id;
        }

        public Cache setId(int id) {
            this.id = id;
            return this;
        }

        public int getLatency() {
            return latency;
        }

        public Cache setLatency(int latency) {
            this.latency = latency;
            return this;
        }

        public int getAvailableSpace() {
            return available;
        }

        public Cache setAvailable(int available) {
            this.available = available;
            return this;
        }

        public ArrayList<Video> getVideos() {
            return videos;
        }

        public boolean hasVideo(Video v) {
            for(int i = 0; i < videos.size(); i++) {
                if(videos.get(i).getId() == v.getId()) return true;
            }

            return false;
        }

        public Cache addVideo(Video v) {
            this.videos.add(v);
            return this;
        }

        @Override
        public int compareTo(Cache o)
        {
            return(latency - o.getLatency());
        }
    }

    public static class Endpoint implements Comparable<Endpoint> {
        int id;
        int latency;
        ArrayList<Cache> caches;

        public int getId() {
            return id;
        }

        public Endpoint setId(int id) {
            this.id = id;
            return this;
        }

        public int getLatency() {
            return latency;
        }

        public Endpoint setLatency(int latency) {
            this.latency = latency;
            return this;
        }

        public ArrayList<Cache> getCaches() {
            return caches;
        }

        public Endpoint setCaches(ArrayList<Cache> caches) {
            this.caches = new ArrayList<>(caches);
            return this;
        }

        @Override
        public int compareTo(Endpoint o) {
//            return latency - o.getLatency();
            return latency - o.getLatency();
        }
    }

    public static class Request implements Comparable<Request> {
        int req_number;
        int video_id;
        int endpoint_id;

        public int getReq_number() {
            return req_number;
        }

        public Request setReq_number(int req_number) {
            this.req_number = req_number;
            return this;
        }

        public int getVideo_id() {
            return video_id;
        }

        public Video searchVideo(ArrayList<Video> videos) {

            return null;
        }

        public Request setVideo_id(int video_id) {
            this.video_id = video_id;
            return this;
        }

        public int getEndpoint_id() {
            return endpoint_id;
        }

        public Request setEndpoint_id(int endpoint_id) {
            this.endpoint_id = endpoint_id;
            return this;
        }

        @Override
        public int compareTo(Request o) {
            return o.getReq_number() - req_number;
        }
    }
}
