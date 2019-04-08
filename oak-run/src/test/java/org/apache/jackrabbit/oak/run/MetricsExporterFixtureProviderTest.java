begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|run
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|io
operator|.
name|prometheus
operator|.
name|client
operator|.
name|exporter
operator|.
name|PushGateway
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|run
operator|.
name|MetricsExporterFixtureProvider
operator|.
name|ExportMetricsArgs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|DefaultWhiteboard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for MetricsExporterFixtureProvider  */
end_comment

begin_class
specifier|public
class|class
name|MetricsExporterFixtureProviderTest
block|{
annotation|@
name|Rule
specifier|public
name|ExpectedException
name|expectedEx
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|checkCorrectPushGatewayInit
parameter_list|()
throws|throws
name|Exception
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|DataStoreOptions
name|dataStoreOptions
init|=
operator|new
name|DataStoreOptions
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|OptionSet
name|option
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"--export-metrics"
argument_list|,
literal|"pushgateway;localhost:9091;key1=value1,key2=value2"
argument_list|)
decl_stmt|;
name|dataStoreOptions
operator|.
name|configure
argument_list|(
name|option
argument_list|)
expr_stmt|;
name|MetricsExporterFixture
name|metricsExporterFixture
init|=
name|MetricsExporterFixtureProvider
operator|.
name|create
argument_list|(
name|dataStoreOptions
argument_list|,
operator|new
name|DefaultWhiteboard
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pushgateway"
argument_list|,
name|metricsExporterFixture
operator|.
name|getExporterType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|metricsExporter
init|=
name|metricsExporterFixture
operator|.
name|getMetricsExporter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|metricsExporter
operator|instanceof
name|PushGateway
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetricArgs
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|option
init|=
literal|"pushgateway;localhost:9091;key1=value1,key2=value2"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expectedMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|expectedMap
operator|.
name|put
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|expectedMap
operator|.
name|put
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|ExportMetricsArgs
name|metricsArgs
init|=
operator|new
name|ExportMetricsArgs
argument_list|(
name|option
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pushgateway"
argument_list|,
name|metricsArgs
operator|.
name|getExporterType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:9091"
argument_list|,
name|metricsArgs
operator|.
name|getPushUri
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedMap
argument_list|,
name|metricsArgs
operator|.
name|getPushMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetricArgsNoType
parameter_list|()
throws|throws
name|Exception
block|{
name|expectedEx
operator|.
name|expect
argument_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|option
init|=
literal|"localhost:9091;key1=value1,key2=value2"
decl_stmt|;
name|ExportMetricsArgs
name|metricsArgs
init|=
operator|new
name|ExportMetricsArgs
argument_list|(
name|option
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetricArgsWrongType
parameter_list|()
throws|throws
name|Exception
block|{
name|expectedEx
operator|.
name|expect
argument_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|option
init|=
literal|"wrongtype:localhost:9091;key1=value1,key2=value2"
decl_stmt|;
name|ExportMetricsArgs
name|metricsArgs
init|=
operator|new
name|ExportMetricsArgs
argument_list|(
name|option
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetricArgsNoProps
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|option
init|=
literal|"pushgateway;localhost:9091"
decl_stmt|;
name|ExportMetricsArgs
name|metricsArgs
init|=
operator|new
name|ExportMetricsArgs
argument_list|(
name|option
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pushgateway"
argument_list|,
name|metricsArgs
operator|.
name|getExporterType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:9091"
argument_list|,
name|metricsArgs
operator|.
name|getPushUri
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Maps
operator|.
name|newHashMap
argument_list|()
argument_list|,
name|metricsArgs
operator|.
name|getPushMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetricArgsNoUrlNoMap
parameter_list|()
throws|throws
name|Exception
block|{
name|expectedEx
operator|.
name|expect
argument_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|option
init|=
literal|"pushgateway"
decl_stmt|;
name|ExportMetricsArgs
name|metricsArgs
init|=
operator|new
name|ExportMetricsArgs
argument_list|(
name|option
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetricArgsNoUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|expectedEx
operator|.
name|expect
argument_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|option
init|=
literal|"pushgateway:key1=value1,key2=value2"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expectedMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|expectedMap
operator|.
name|put
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|expectedMap
operator|.
name|put
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|ExportMetricsArgs
name|metricsArgs
init|=
operator|new
name|ExportMetricsArgs
argument_list|(
name|option
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pushgateway"
argument_list|,
name|metricsArgs
operator|.
name|getExporterType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|expectedMap
argument_list|,
name|metricsArgs
operator|.
name|getPushMap
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"localhost:9091"
argument_list|,
name|metricsArgs
operator|.
name|getPushUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
