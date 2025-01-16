> Reusable clj uberjar and jpackage code.
>

```clojure
; in deps.edn
:aliases {
          ...
           :build-tools
           {:deps      
            {build-tools/build-tools
             {:git/url "https://github.com/hellonico/build-tools.git"
              :sha "1c180d133ac592bb1fc2eb7241494da5d84b314d"}
             ;{:local/root "../../build-tools"}
             }
            ;:exec-fn   build-tools.core/build-all
            :exec-args {:app-name "Collagen" :mainns ppt-collate :compile [ppt-collate]}}}
```

And then, to create uber jar file:
```bash
clj -X:build-tools build-tools.core/uberize
```

Or jpackage:

```bash
clj -X:build-tools build-tools.core/jpackage
```
