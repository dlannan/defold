(ns dev
  (:require [clojure.java.io :as io]
            [clojure.java.javadoc :refer (javadoc)]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.reflect :refer (reflect)]
            [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :as repl :refer (clear refresh refresh-all)]
            [clojure.tools.trace :refer (trace deftrace trace-forms trace-ns trace-vars)]
            [editor.boot]
            [suite]
            [dynamo.graph :as g]
            [internal.graph.types :as gt]
            [internal.java :as java])
  (:import [com.defold.editor DevStart]))

(defn start []
  (def jfx-app (future (DevStart/main (into-array String [])))))

(def go start)

(defn run-all-my-tests []
  (refresh)
  (suite/suite))

(defmacro tap [x] `(do (prn ~(str "**** " &form " ") ~x) ~x))

(defn macro-pretty-print
  [x]
  (clojure.pprint/write (macroexpand x) :dispatch clojure.pprint/code-dispatch))

(defn nodes
  []
  (mapcat (comp vals :nodes) (vals (:graphs (g/now)))))

(defn nodes-and-classes
  []
  (for [n (nodes)
        :let [node-id (g/node-id n)]]
    [(gt/node-id->graph-id node-id) (gt/node-id->nid node-id) (some-> n :node-type deref :name)]))

(defn node
  ([node-id]
   (g/node-by-id (g/now) node-id))

  ([gid nid]
   (g/node-by-id (g/now) (gt/make-node-id gid nid))))

(defn node-at
  ([basis node-id]
   (g/node-by-id basis node-id))

  ([basis gid nid]
   (g/node-by-id basis (gt/make-node-id gid nid))))

(defn nodes-for-file
  [filename]
  (for [n (nodes)
        :let [f (some-> n :resource :file (.getPath))]
        :when (and f (.endsWith f filename))]
    n))

(defn node-type
  ([node-id]
   (-> (node node-id) g/node-type :name))
  ([gid nid]
   (-> (node gid nid) g/node-type :name)))

(defn inputs-to
  ([node-id label]
   (sort-by first
            (gt/sources (g/now) node-id label)))
  ([gid nid label]
   (inputs-to (gt/make-node-id gid nid) label)))

(defn inputs-to-at
  ([basis node-id label]
   (sort-by first
            (gt/sources basis node-id label)))
  ([basis gid nid label]
   (inputs-to basis (gt/make-node-id gid nid) label)))

(defn outputs-from
  ([node-id label]
   (sort-by first
            (gt/targets (g/now) node-id label)))
  ([gid nid label]
   (outputs-from (gt/make-node-id gid nid) label)))

(defn all-outputs
  ([node-id]
   (map #(outputs-from node-id %) (-> (node node-id) g/node-type g/output-labels)))
  ([gid nid]
   (all-outputs (gt/make-node-id gid nid))))

(defn get-value
  [gid nid label]
  (g/node-value (node gid nid) label))
