package ro.adlabs;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int _videos = 0, _endpoints = 0, _requests, _caches = 0, _cache_size;
    private Classes.Video videos[];
    private Classes.Endpoint endpoints[];
    private Classes.Request requests[];
    private Classes.Cache caches[];
    private float requests_total = 0;

    private void start() throws IOException {

        readDataFromFile("kittens.in");
        ArrayList<Classes.Cache> caches = processRequestsToCache(Classes.Request.class.getName());

        writeDataToFile("outputs.out", caches);


        System.out.print("salut");

    }

    //proceseaza requesturile si pune videos in cache in functie de numarul de requesturi per video per endpoint
    private ArrayList<Classes.Cache> processRequestsToCache(String className) {
        ArrayList<Classes.Request> $requests = new ArrayList<>(Arrays.asList(requests));
        ArrayList<Classes.Video> $videos = new ArrayList<>(Arrays.asList(videos));
        ArrayList<Classes.Cache> $caches = new ArrayList<>(Arrays.asList(caches));
        ArrayList<Classes.Endpoint> $endpoints = new ArrayList<>(Arrays.asList(endpoints));

        if (className.equals(Classes.Request.class.getName())) {
            //trying to process by requests
            Collections.sort($requests);
            ArrayList<Classes.Cache> usedCaches = new ArrayList<>();
            for (Classes.Request request : $requests) {
                Classes.Endpoint e = getEndpoint(request.getEndpoint_id(), $endpoints);
                Classes.Video v = getVideo(request.getVideo_id(), $videos);

                assert e != null;
                ArrayList<Classes.Cache> endpointCaches = e.getCaches();
                Collections.sort(endpointCaches);

                for (Classes.Cache c : endpointCaches) {
                    assert v != null;
                    if (!c.hasVideo(v) /*&& (c.getAvailableSpace() >= v.getSize())*/) {
                        c.addVideo(v);
                        c.setAvailable(c.getAvailableSpace() - v.getSize());

                        if (!alreadyHasCache(c, usedCaches)) {
                            usedCaches.add(c);
                        }
                        break;
                    }
                }
            }

            return usedCaches;
        } else if (className.equals(Classes.Video.class.getName())) {
            //trying to process by videos
            Collections.sort($caches);

            for (Classes.Video v : $videos) {
                for (Classes.Cache c : $caches) {
                    if (!c.hasVideo(v) && (c.getAvailableSpace() >= v.getSize())) {
                        c.addVideo(v);
                        c.setAvailable(c.getAvailableSpace() - v.getSize());
                        break;
                    }
                }
            }

            return $caches;
        } else if (className.equals(Classes.Endpoint.class.getName())) {
            //caut toate video-urile pentru un cache si le bag in el pe cele cu latency cel mai mare;
            ArrayList<Classes.Cache> finalCaches = processRequestsToCache(Classes.Request.class.getName());
            ArrayList<Classes.Cache> usedCaches = new ArrayList<>();

            for (Classes.Cache c : finalCaches) {
                System.out.println("Processing cache #" + c.getId());
                ArrayList<Classes.Video> videos = c.getVideos();
                ArrayList<Classes.Endpoint> endpoints = getEndpointListForCache(c, $endpoints);
                Collections.sort(endpoints);

                for (Classes.Endpoint e : endpoints) {
                    ArrayList<Classes.Request> requests = getRequestsForEndpoint(e, $requests);
                    Collections.sort(requests);

                    for (Classes.Request r : requests) {
                        Classes.Video v = getVideo(r.getVideo_id(), $videos);

                        if (!c.hasVideo(v) && (c.getAvailableSpace() >= v.getSize())) {
                            c.addVideo(v);
                            c.setAvailable(c.getAvailableSpace() - v.getSize());

                            if (!alreadyHasCache(c, usedCaches)) {
                                usedCaches.add(c);
                            }
                            break;
                        }
                    }
                }
            }

            return usedCaches;
        }

        return null;
    }

    private ArrayList<Classes.Request> getRequestsForEndpoint(Classes.Endpoint e, ArrayList<Classes.Request> $requests) {
        ArrayList<Classes.Request> finalRequests = new ArrayList<>();

        for (Classes.Request r : $requests) {
            if (r.getEndpoint_id() == e.getId()) {
                finalRequests.add(r);
            }
        }

        return finalRequests;
    }

    private ArrayList<Classes.Endpoint> getEndpointListForCache(Classes.Cache c, ArrayList<Classes.Endpoint> $endpoints) {
        ArrayList<Classes.Endpoint> finalEndpoints = new ArrayList<>();

        for (Classes.Endpoint e : $endpoints) {
            ArrayList<Classes.Cache> endpointCaches = e.getCaches();
            for (Classes.Cache cache : endpointCaches) {
                if (cache.getId() == c.getId()) {
                    finalEndpoints.add(e);
                }
            }
        }

        return finalEndpoints;
    }

    private boolean alreadyHasCache(Classes.Cache c, ArrayList<Classes.Cache> usedCaches) {
        for (Classes.Cache cache : usedCaches) {
            if (cache.getId() == c.getId()) return true;
        }

        return false;
    }

    private Classes.Endpoint getEndpoint(int endpoint_id, ArrayList<Classes.Endpoint> endpoints) {
        for (Classes.Endpoint e : endpoints) {
            if (e.getId() == endpoint_id) return e;
        }

        return null;
    }

    private Classes.Video getVideo(int video_id, ArrayList<Classes.Video> $videos) {
        for (Classes.Video v : $videos) {
            if (v.getId() == video_id) return v;
        }

        return null;
    }

    private void writeDataToFile(String fName, ArrayList<Classes.Cache> caches) throws IOException {
        PrintStream ps = new PrintStream(new File(fName));

        int cachesUsed = getNumberOfCachesUsed(caches);
        ps.println(cachesUsed);

        for (Classes.Cache c : caches) {
            StringBuilder builder = new StringBuilder();

            int cache_id = c.getId();
            builder.append(cache_id).append(" ");

            ArrayList<Classes.Video> usedVideos = c.getVideos();
            if (usedVideos.size() > 0) {
                for (Classes.Video v : usedVideos) {
                    builder.append(v.getId()).append(" ");
                }

                ps.println(builder.toString());
                System.out.println("Cache #" + c.getId() + " has " + c.getAvailableSpace() + " mb available ");
            }
        }

        ps.close();
    }

    private void feedCaches() {
        ArrayList<Classes.Cache> finalCaches = new ArrayList<>();
        for (int i = 0; i < _endpoints; i++) {
            ArrayList<Classes.Cache> endpointCaches = endpoints[i].getCaches();
            for (Classes.Cache c : endpointCaches) {
                if (!has(finalCaches, c)) {
                    finalCaches.add(c);
                }
            }
        }

        caches = finalCaches.toArray(new Classes.Cache[finalCaches.size()]);
    }

    private boolean has(ArrayList<Classes.Cache> finalCaches, Classes.Cache c) {
        for (Classes.Cache finalCache : finalCaches) {
            if (finalCache.getId() == c.getId()) return true;
        }

        return false;
    }

    private int getNumberOfCachesUsed(ArrayList<Classes.Cache> finalCaches) {
        int counter = 0;
        for (Classes.Cache cache : finalCaches) {
            if (cache.getAvailableSpace() < _cache_size) counter++;
        }

        return counter;
    }

    private void readDataFromFile(String fName) throws FileNotFoundException {

        FileInputStream fos = new FileInputStream(new File(fName));
        Scanner s = new Scanner(fos);
        _videos = s.nextInt();
        _endpoints = s.nextInt();
        _requests = s.nextInt();
        _caches = s.nextInt();
        _cache_size = s.nextInt();

        videos = new Classes.Video[_videos];
        endpoints = new Classes.Endpoint[_endpoints];
        requests = new Classes.Request[_requests];
        caches = new Classes.Cache[_caches];

        for (int i = 0; i < _videos; i++) {
            int size = s.nextInt();

            videos[i] = new Classes.Video()
                    .setId(i)
                    .setSize(size);
        }

        for (int i = 0; i < _endpoints; i++) {
            int latency = s.nextInt();
            int no_caches = s.nextInt();

            ArrayList<Classes.Cache> cachesList = new ArrayList<>();
            for (int j = 0; j < no_caches; j++) {
                int cache_id = s.nextInt();
                int cache_lat = s.nextInt();

                Classes.Cache c = new Classes.Cache()
                        .setId(cache_id)
                        .setLatency(cache_lat)
                        .setAvailable(_cache_size);
                cachesList.add(c);
            }

            endpoints[i] = new Classes.Endpoint()
                    .setId(i)
                    .setLatency(latency)
                    .setCaches(cachesList);
        }

        for (int i = 0; i < _requests; i++) {
            int vid_id = s.nextInt();
            int endpoint_id = s.nextInt();
            int no_req = s.nextInt();
            requests_total += no_req;

            requests[i] = new Classes.Request()
                    .setVideo_id(vid_id)
                    .setEndpoint_id(endpoint_id)
                    .setReq_number(no_req);
        }

        feedCaches();
    }
}
