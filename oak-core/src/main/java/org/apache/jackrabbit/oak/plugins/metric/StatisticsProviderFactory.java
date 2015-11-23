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
name|plugins
operator|.
name|metric
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|PropertyOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferenceCardinality
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
name|commons
operator|.
name|PropertiesUtil
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
name|stats
operator|.
name|DefaultStatisticsProvider
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
name|stats
operator|.
name|StatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Factory to create StatisticsProvider depending on setup. It detects if the  * {@link Metrics http://metrics.dropwizard.io} library is present or not. If present  * then it configures a MetricsStatisticsProvider otherwise fallbacks to DefaultStatisticsProvider  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak StatisticsProviderFactory"
argument_list|,
name|description
operator|=
literal|"Creates a statistics providers used by Oak. By default if checks if Metrics ("
operator|+
literal|"See http://metrics.dropwizard.io) library is present then that is used. Otherwise it fallbacks "
operator|+
literal|"to default"
argument_list|)
specifier|public
class|class
name|StatisticsProviderFactory
block|{
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|TYPE_AUTO
argument_list|,
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|TYPE_DEFAULT
argument_list|,
name|value
operator|=
name|TYPE_DEFAULT
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|TYPE_METRIC
argument_list|,
name|value
operator|=
name|TYPE_METRIC
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|TYPE_NONE
argument_list|,
name|value
operator|=
name|TYPE_NONE
argument_list|)
block|}
argument_list|)
specifier|static
specifier|final
name|String
name|PROVIDER_TYPE
init|=
literal|"providerType"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_DEFAULT
init|=
literal|"DEFAULT"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_METRIC
init|=
literal|"METRIC"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_NONE
init|=
literal|"NONE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_AUTO
init|=
literal|"AUTO"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|METRIC_PROVIDER_CLASS
init|=
literal|"com.codahale.metrics.MetricRegistry"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Keeping this as optional as for default case MBeanServer is not required      * Further Metrics would bound to default platform MBeanServer is no explicit      * server is provided.      */
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL_UNARY
argument_list|)
specifier|private
name|MBeanServer
name|server
decl_stmt|;
specifier|private
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
name|ServiceRegistration
name|registration
decl_stmt|;
specifier|private
name|ScheduledExecutorService
name|executor
decl_stmt|;
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
name|String
name|providerType
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROVIDER_TYPE
argument_list|)
argument_list|,
name|TYPE_AUTO
argument_list|)
decl_stmt|;
name|statisticsProvider
operator|=
name|createProvider
argument_list|(
name|providerType
argument_list|)
expr_stmt|;
if|if
condition|(
name|statisticsProvider
operator|!=
literal|null
condition|)
block|{
name|registration
operator|=
name|context
operator|.
name|registerService
argument_list|(
name|StatisticsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|statisticsProvider
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|registration
operator|!=
literal|null
condition|)
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|statisticsProvider
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|statisticsProvider
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//TODO Refactor ExecutorCloser in Oak as a utility class and
comment|//use that here
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|StatisticsProvider
name|createProvider
parameter_list|(
name|String
name|providerType
parameter_list|)
block|{
if|if
condition|(
name|TYPE_NONE
operator|.
name|equals
argument_list|(
name|providerType
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No statistics provider created as {} option is selected"
argument_list|,
name|TYPE_NONE
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|executor
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|String
name|effectiveProviderType
init|=
name|providerType
decl_stmt|;
if|if
condition|(
name|TYPE_AUTO
operator|.
name|equals
argument_list|(
name|providerType
argument_list|)
operator|&&
name|isMetricSupportPresent
argument_list|()
condition|)
block|{
name|effectiveProviderType
operator|=
name|TYPE_METRIC
expr_stmt|;
block|}
if|if
condition|(
name|TYPE_METRIC
operator|.
name|equals
argument_list|(
name|effectiveProviderType
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Using MetricsStatisticsProvider"
argument_list|)
expr_stmt|;
return|return
name|createMetricsProvider
argument_list|(
name|executor
argument_list|)
return|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Using DefaultStatisticsProvider"
argument_list|)
expr_stmt|;
return|return
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executor
argument_list|)
return|;
block|}
specifier|private
name|StatisticsProvider
name|createMetricsProvider
parameter_list|(
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|metric
operator|.
name|MetricStatisticsProvider
argument_list|(
name|server
argument_list|,
name|executor
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isMetricSupportPresent
parameter_list|()
block|{
try|try
block|{
name|StatisticsProviderFactory
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|METRIC_PROVIDER_CLASS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Cannot load optional Metrics library support"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

