# json scripting
Json template input use as a script to generate json outputs

Example : Sample output template
``` json
{
  "dateTime" : "$s$dateNow",
  "date" : "$f$date(MM/dd/yyyy)",
  "twoParams" : "$bf$twoParams(param1,param2)",
  "threeParams" : "$tf$threeParams(param1,param2,param3)"
}
```
After evaluation above rules by the rule engine, it will produce below output.
``` json
{
  "dateTime" : "2020-06-14T14:03:37.443",
  "date" : "06/14/2020",
  "twoParams" : "param1_param2",
  "threeParams" : "param1+param2+param3"
}
```
==========
Above json template can handle any complex structure of json(arrays,maps)
