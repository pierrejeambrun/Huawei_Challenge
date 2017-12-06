# Huawei Challenge


### I Introduction
This is the code used for the  **Huawei Challenge**.
You can find the code of the android application in the `Android_Studio` module and the code of
the machine learning module in `Ml_Module`.

### II Installation
You will need to install and configure the **LAMP** stack (Linux Apache MySQL Php), you can see how
to install it [here](https://doc.ubuntu-fr.org/lamp).

To install the dependencies for the **machine learning module** you can go into the folder `ml_module`
and run:

``` sh
pip install -r requirements.txt

```

### TIPS
Android Sdk version : 26  
Build tools Sdk : 26.0.2  

#### HTTP Request : Volley lib
The tutorial for this lib is [there](https://developer.android.com/training/volley/index.html).

* First get latest volley with git (git clone https://android.googlesource.com/platform/frameworks/volley).
* In your current project (android studio) click [File] --> [New] -->[Import Module].
* Now select the directory where you downloaded Volley to.
* Now Android studio might guide you to do the rest but continue guide to verify that everything works correct
* Open settings.gradle (find in root) and add (or verify this is included):
``` java
include ':app', ':volley'
```
* Now go to your build.gradle in your project and add the dependency:
``` java
compile project(":volley")
```
