A basic ListView populated with text and images from a JSON feed. Layouts adjust for portrait/landscape as well as for tablets.

An IntentService was used along with OkHttp to download the JSON string. It was then parsed using GSON and inserted into a ContentProvider. A cursor loader was used to populate the listview from the ContentProvider, and Picasso downloaded, cached, and injected the images from their urls.
