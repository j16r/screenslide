(ns screenslide.util)

(defmacro interval [display timeout & body]
  "Create a repeating SWT timer"
  `(.timerExec ~display ~timeout
    (proxy [Runnable][]
      (run []
        (do
          ~@body
          (.timerExec ~display ~timeout ~'this))))))
