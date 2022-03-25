# SimpleTodoService
This is the backend service for the SimpleTodo Android app. This service was built with Scala and the Play Framework.

To find out more information, please check out the [Android app repo](https://github.com/MackHartley/simpleTodoAndroid) of this project.

## Running Locally
If you want to test the Android app and Scala server locally you can do the following:
1) Switch the server host address to `10.0.2.2:9000`. Do that by commenting out [this code](https://github.com/MackHartley/SimpleTodoService/blob/master/conf/application.conf#L23) and uncomment [this code](https://github.com/MackHartley/SimpleTodoService/blob/master/conf/application.conf#L22)
2) Switch the Android app to [send requests](https://github.com/MackHartley/simpleTodoAndroid/blob/master/app/src/main/java/com/mackhartley/simpletodo/common/network/RetrofitConfig.kt#L4) to `10.0.2.2:9000`
3) Spin up a local Postgres DB. If you're not sure how to do that, [this video](https://www.youtube.com/watch?v=P9O1BuuUDBY&list=PLLMXbkbDbVt8tBiGc1y69BZdG8at1D7ZF&index=60) will be helpful.
