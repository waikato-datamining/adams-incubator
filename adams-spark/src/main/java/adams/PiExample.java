/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * PiExample.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Spark directory:
 * /home/fracpete/programs/spark-2.2.1-bin-hadoop2.7
 *
 * Starting up:
 * ./sbin/start-master.sh -h 192.168.2.7
 * ./sbin/start-slave.sh  spark://192.168.2.7:7077
 *
 * Stopping:
 * ./sbin/stop-all.sh
 *
 * http://spark.apache.org/examples.html
 * http://letsprog.com/apache-spark-tutorial-rdd-transformation-action-examples/
 */
public class PiExample {

  public static void main(String[] args) {
    SparkConf sparkConf = new SparkConf();

    sparkConf.setAppName("Hello Spark");
    sparkConf.setMaster("local");

    JavaSparkContext context = new JavaSparkContext(sparkConf);

    int slices = (args.length == 1) ? Integer.parseInt(args[0]) : 2;
    int n = 100000 * slices;
    List<Integer> l = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      l.add(i);
    }

    JavaRDD<Integer> dataSet = context.parallelize(l, slices);

    int count = dataSet.map(integer -> {
      double x = Math.random() * 2 - 1;
      double y = Math.random() * 2 - 1;
      return (x * x + y * y <= 1) ? 1 : 0;
    }).reduce((integer, integer2) -> integer + integer2);

    System.out.println("Pi is roughly " + 4.0 * count / n);

    context.stop();
  }
}