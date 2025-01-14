(ns build-tools.core
  (:require
    [clojure.java.io :as io]
    [clojure.pprint]
    [clojure.java.shell :as shell]
    [clojure.string :as str]
    [clojure.tools.build.api :as b]))

;(def app-name "linen")
(def version (format "1.0.%s" (or (b/git-count-revs nil) "no.git")))
(def class-dir "target/classes")
;(def uber-file (format "target/%s-%s.jar" app-name version))

;; delay to defer side effects (artifact downloads)
(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber-file[app-name]
  (format "target/%s-%s-jdk%s.jar" (str/lower-case app-name) version (System/getProperty "java.version")))

(defn uberize [{:keys [app-name compile mainns] :as options}]
  ;(clojure.pprint/pprint options)

  (clean nil)
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis      @basis
                  :ns-compile compile
                  :class-dir  class-dir})
  (b/uber {:class-dir class-dir
           :uber-file (uber-file app-name)
           :basis     @basis
           :main      mainns
           }))
(defn app-icon[{app-name :app-name}]
  (let[ os (System/getProperty "os.name")
        file-extension (if (or (.contains os "Mac") (= os "Mac OS X")) ".icns" ".png")
       ]
    (str "resources/" (str/lower-case app-name) file-extension)
  ))
(defn jpackage [{app-name :app-name :as options} ]
  (shell/sh "jpackage"
            "--dest" "output"
            "--name" app-name
            "--input" (.getParent (io/as-file (uber-file app-name)))
            "--java-options" "-Xmx2048m"
            "--main-jar" (.getName (io/as-file (uber-file app-name)))
            "--icon" (app-icon options)
            "--app-version" version))

(defn build-all[options]
  (uberize options)
  (jpackage options))

;(defn uber-linen [_]
;  (uberize {:app-name "linen" :compile '[linen.core] :main 'linen.core}))
