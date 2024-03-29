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
operator|.
name|osgi
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ServiceLoader
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
name|CountDownLatch
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
name|ExecutionException
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
name|TimeUnit
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
name|TimeoutException
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeList
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|Lists
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
name|util
operator|.
name|concurrent
operator|.
name|SettableFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
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
name|connect
operator|.
name|launch
operator|.
name|BundleDescriptor
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
name|connect
operator|.
name|launch
operator|.
name|ClasspathScanner
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
name|connect
operator|.
name|launch
operator|.
name|PojoServiceRegistry
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
name|connect
operator|.
name|launch
operator|.
name|PojoServiceRegistryFactory
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
name|api
operator|.
name|JackrabbitRepository
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
name|osgi
operator|.
name|framework
operator|.
name|Bundle
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
name|BundleActivator
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
name|BundleEvent
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
name|BundleException
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
name|Constants
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
name|ServiceReference
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
name|SynchronousBundleListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|util
operator|.
name|tracker
operator|.
name|ServiceTracker
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
comment|/**  * RepositoryFactory which constructs an instance of Oak repository. Thi factory supports following  * parameters  *  *<dl>  *<dt>org.osgi.framework.BundleActivator</dt>  *<dd>(Optional) BundleActivator instance which would be notified about the startup and shutdown</dd>  *  *<dt>org.apache.jackrabbit.oak.repository.config</dt>  *<dd>(Optional) Config key which refers to the map of config where key in that map refers to OSGi config</dd>  *  *<dt>org.apache.jackrabbit.oak.repository.configFile</dt>  *<dd>  *          Comma separated list of file names which referred to config stored in form of JSON. The  *          JSON content consist of pid as the key and config map as the value  *</dd>  *  *<dt>org.apache.jackrabbit.repository.home</dt>  *<dd>Used to specify the absolute path of the repository home directory</dd>  *  *<dt>org.apache.jackrabbit.oak.repository.bundleFilter</dt>  *<dd>Used to specify the bundle filter string which is passed to ClasspathScanner</dd>  *  *<dt>org.apache.jackrabbit.oak.repository.timeoutInSecs</dt>  *<dd>Timeout in seconds for the repository startup/shutdown should wait. Defaults to 10 minutes</dd>  *  *<dt>org.apache.jackrabbit.oak.repository.shutDownOnTimeout</dt>  *<dd>Boolean flag to determine if the OSGi container should be shutdown upon timeout. Defaults to false</dd>  *</dl>  */
end_comment

begin_class
specifier|public
class|class
name|OakOSGiRepositoryFactory
implements|implements
name|RepositoryFactory
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OakOSGiRepositoryFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Name of the repository home parameter.      */
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_HOME
init|=
literal|"org.apache.jackrabbit.repository.home"
decl_stmt|;
comment|/**      * Timeout in seconds for the repository startup should wait      */
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_TIMEOUT_IN_SECS
init|=
literal|"org.apache.jackrabbit.oak.repository.timeoutInSecs"
decl_stmt|;
comment|/**      * Config key which refers to the map of config where key in that map refers to OSGi      * config      */
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_CONFIG
init|=
literal|"org.apache.jackrabbit.oak.repository.config"
decl_stmt|;
comment|/**      * Comma separated list of file names which referred to config stored in form of JSON. The      * JSON content consist of pid as the key and config map as the value      */
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_CONFIG_FILE
init|=
literal|"org.apache.jackrabbit.oak.repository.configFile"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_BUNDLE_FILTER
init|=
literal|"org.apache.jackrabbit.oak.repository.bundleFilter"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_SHUTDOWN_ON_TIMEOUT
init|=
literal|"org.apache.jackrabbit.oak.repository.shutDownOnTimeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_ENV_SPRING_BOOT
init|=
literal|"org.apache.jackrabbit.oak.repository.springBootMode"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_BUNDLE_FILTER_DEFAULT
init|=
literal|"(|"
operator|+
literal|"(Bundle-SymbolicName=org.apache.jackrabbit*)"
operator|+
literal|"(Bundle-SymbolicName=org.apache.sling*)"
operator|+
literal|"(Bundle-SymbolicName=org.apache.felix*)"
operator|+
literal|"(Bundle-SymbolicName=org.apache.aries*)"
operator|+
literal|"(Bundle-SymbolicName=groovy-all)"
operator|+
literal|")"
decl_stmt|;
comment|/**      * Default timeout for repository creation      */
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_TIMEOUT
init|=
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toSeconds
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BundleActivator
name|NOOP
init|=
operator|new
name|BundleActivator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
throws|throws
name|Exception
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
throws|throws
name|Exception
block|{          }
block|}
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Repository
name|getRepository
parameter_list|(
name|Map
name|parameters
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|parameters
operator|==
literal|null
operator|||
operator|!
name|parameters
operator|.
name|containsKey
argument_list|(
name|REPOSITORY_HOME
argument_list|)
condition|)
block|{
comment|//Required param missing so repository cannot be created
return|return
literal|null
return|;
block|}
name|Map
name|config
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|config
operator|.
name|putAll
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
name|PojoServiceRegistry
name|registry
init|=
name|initializeServiceRegistry
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|BundleActivator
name|activator
init|=
name|getApplicationActivator
argument_list|(
name|config
argument_list|)
decl_stmt|;
try|try
block|{
name|activator
operator|.
name|start
argument_list|(
name|registry
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while starting activator {}"
argument_list|,
name|activator
operator|.
name|getClass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//Future which would be used to notify when repository is ready
comment|// to be used
name|SettableFuture
argument_list|<
name|Repository
argument_list|>
name|repoFuture
init|=
name|SettableFuture
operator|.
name|create
argument_list|()
decl_stmt|;
operator|new
name|RunnableJobTracker
argument_list|(
name|registry
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|timeoutInSecs
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|REPOSITORY_TIMEOUT_IN_SECS
argument_list|)
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|)
decl_stmt|;
comment|//Start the tracker for repository creation
operator|new
name|RepositoryTracker
argument_list|(
name|registry
argument_list|,
name|activator
argument_list|,
name|repoFuture
argument_list|,
name|timeoutInSecs
argument_list|)
expr_stmt|;
comment|//Now wait for repository to be created with given timeout
comment|//if repository creation takes more time. This is required to handle case
comment|// where OSGi runtime fails to start due to bugs (like cycles)
try|try
block|{
return|return
name|repoFuture
operator|.
name|get
argument_list|(
name|timeoutInSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Repository initialization was interrupted"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|REPOSITORY_SHUTDOWN_ON_TIMEOUT
argument_list|)
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|shutdown
argument_list|(
name|registry
argument_list|,
name|timeoutInSecs
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"OSGi container shutdown after waiting for repository initialization for {} sec"
argument_list|,
name|timeoutInSecs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] found to be false. Container is not stopped"
argument_list|,
name|REPOSITORY_SHUTDOWN_ON_TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|BundleException
name|be
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while shutting down the service registry (due to "
operator|+
literal|"startup timeout) backing the Repository "
argument_list|,
name|be
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Repository could not be started in "
operator|+
name|timeoutInSecs
operator|+
literal|" seconds"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|PojoServiceRegistry
name|initializeServiceRegistry
parameter_list|(
name|Map
name|config
parameter_list|)
block|{
name|processConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|PojoServiceRegistry
name|registry
init|=
name|createServiceRegistry
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|registerMBeanServer
argument_list|(
name|registry
argument_list|)
expr_stmt|;
name|startConfigTracker
argument_list|(
name|registry
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|preProcessRegistry
argument_list|(
name|registry
argument_list|)
expr_stmt|;
name|startBundles
argument_list|(
name|registry
argument_list|,
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|REPOSITORY_BUNDLE_FILTER
argument_list|)
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|postProcessRegistry
argument_list|(
name|registry
argument_list|)
expr_stmt|;
return|return
name|registry
return|;
block|}
comment|/**      * Enables pre processing of service registry by sub classes. This can be      * used to register services before any bundle gets started      *      * @param registry service registry      */
specifier|protected
name|void
name|preProcessRegistry
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|)
block|{      }
comment|/**      * Enables post processing of service registry e.g. registering new services etc      * by sub classes      *      * @param registry service registry      */
specifier|protected
name|void
name|postProcessRegistry
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|)
block|{      }
specifier|protected
name|List
argument_list|<
name|BundleDescriptor
argument_list|>
name|processDescriptors
parameter_list|(
name|List
argument_list|<
name|BundleDescriptor
argument_list|>
name|descriptors
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|descriptors
argument_list|,
operator|new
name|BundleDescriptorComparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|descriptors
return|;
block|}
specifier|static
name|void
name|shutdown
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|,
name|int
name|timeoutInSecs
parameter_list|)
throws|throws
name|BundleException
block|{
if|if
condition|(
name|registry
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|Bundle
name|systemBundle
init|=
name|registry
operator|.
name|getBundleContext
argument_list|()
operator|.
name|getBundle
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|shutdownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|//Logic here is similar to org.apache.felix.connect.PojoServiceRegistryFactoryImpl.FrameworkImpl.waitForStop()
name|systemBundle
operator|.
name|getBundleContext
argument_list|()
operator|.
name|addBundleListener
argument_list|(
operator|new
name|SynchronousBundleListener
argument_list|()
block|{
specifier|public
name|void
name|bundleChanged
parameter_list|(
name|BundleEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getBundle
argument_list|()
operator|==
name|systemBundle
operator|&&
name|event
operator|.
name|getType
argument_list|()
operator|==
name|BundleEvent
operator|.
name|STOPPED
condition|)
block|{
name|shutdownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|//Initiate shutdown
name|systemBundle
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|//Wait for framework shutdown to complete
try|try
block|{
name|boolean
name|shutdownWithinTimeout
init|=
name|shutdownLatch
operator|.
name|await
argument_list|(
name|timeoutInSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|shutdownWithinTimeout
condition|)
block|{
throw|throw
operator|new
name|BundleException
argument_list|(
literal|"Timed out while waiting for repository "
operator|+
literal|"shutdown for "
operator|+
name|timeoutInSecs
operator|+
literal|" secs"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|BundleException
argument_list|(
literal|"Timed out while waiting for repository "
operator|+
literal|"shutdown for "
operator|+
name|timeoutInSecs
operator|+
literal|" secs"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|startConfigTracker
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|,
name|Map
name|config
parameter_list|)
block|{
operator|new
name|ConfigTracker
argument_list|(
name|config
argument_list|,
name|registry
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the BundleActivator provided by the embedding application      * @param config config passed to factory for initialization      * @return BundleActivator instance      */
specifier|private
specifier|static
name|BundleActivator
name|getApplicationActivator
parameter_list|(
name|Map
name|config
parameter_list|)
block|{
name|BundleActivator
name|activator
init|=
operator|(
name|BundleActivator
operator|)
name|config
operator|.
name|get
argument_list|(
name|BundleActivator
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|activator
operator|==
literal|null
condition|)
block|{
name|activator
operator|=
name|NOOP
expr_stmt|;
block|}
return|return
name|activator
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
name|void
name|processConfig
parameter_list|(
name|Map
name|config
parameter_list|)
block|{
name|String
name|home
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|REPOSITORY_HOME
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|home
argument_list|,
literal|"Repository home not defined via [%s]"
argument_list|,
name|REPOSITORY_HOME
argument_list|)
expr_stmt|;
name|home
operator|=
name|FilenameUtils
operator|.
name|normalizeNoEndSeparator
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|String
name|bundleDir
init|=
name|FilenameUtils
operator|.
name|concat
argument_list|(
name|home
argument_list|,
literal|"bundles"
argument_list|)
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
name|Constants
operator|.
name|FRAMEWORK_STORAGE
argument_list|,
name|bundleDir
argument_list|)
expr_stmt|;
comment|//FIXME Pojo SR currently reads this from system property instead of Framework Property
name|config
operator|.
name|put
argument_list|(
name|Constants
operator|.
name|FRAMEWORK_STORAGE
argument_list|,
name|bundleDir
argument_list|)
expr_stmt|;
comment|//Directory used by Felix File Install to watch for configs
name|config
operator|.
name|put
argument_list|(
literal|"felix.fileinstall.dir"
argument_list|,
name|FilenameUtils
operator|.
name|concat
argument_list|(
name|home
argument_list|,
literal|"config"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Set log level for config to INFO LogService.LOG_INFO
name|config
operator|.
name|put
argument_list|(
literal|"felix.fileinstall.log.level"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
comment|//This ensures that configuration is registered in main thread
comment|//and not in a different thread
name|config
operator|.
name|put
argument_list|(
literal|"felix.fileinstall.noInitialDelay"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"repository.home"
argument_list|,
name|FilenameUtils
operator|.
name|concat
argument_list|(
name|home
argument_list|,
literal|"repository"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PojoServiceRegistry
name|createServiceRegistry
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
try|try
block|{
name|ServiceLoader
argument_list|<
name|PojoServiceRegistryFactory
argument_list|>
name|loader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|PojoServiceRegistryFactory
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|loader
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|newPojoServiceRegistry
argument_list|(
name|config
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|startBundles
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|,
name|String
name|bundleFilter
parameter_list|,
name|Map
name|config
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|bundleFilter
operator|==
literal|null
condition|)
block|{
name|bundleFilter
operator|=
name|REPOSITORY_BUNDLE_FILTER_DEFAULT
expr_stmt|;
block|}
name|List
argument_list|<
name|BundleDescriptor
argument_list|>
name|descriptors
init|=
operator|new
name|ClasspathScanner
argument_list|()
operator|.
name|scanForBundles
argument_list|(
name|bundleFilter
argument_list|)
decl_stmt|;
name|descriptors
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|descriptors
argument_list|)
expr_stmt|;
if|if
condition|(
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|REPOSITORY_ENV_SPRING_BOOT
argument_list|)
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|descriptors
operator|=
name|SpringBootSupport
operator|.
name|processDescriptors
argument_list|(
name|descriptors
argument_list|)
expr_stmt|;
block|}
name|descriptors
operator|=
name|processDescriptors
argument_list|(
name|descriptors
argument_list|)
expr_stmt|;
name|registry
operator|.
name|startBundles
argument_list|(
name|descriptors
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Registers the Platform MBeanServer as OSGi service. This would enable      * Aries JMX Whitboard support to then register the JMX MBean which are registered as OSGi service      * to be registered against the MBean server      */
specifier|private
specifier|static
name|void
name|registerMBeanServer
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|)
block|{
name|MBeanServer
name|platformMBeanServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mbeanProps
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|ObjectName
name|beanName
init|=
name|ObjectName
operator|.
name|getInstance
argument_list|(
literal|"JMImplementation:type=MBeanServerDelegate"
argument_list|)
decl_stmt|;
name|AttributeList
name|attrs
init|=
name|platformMBeanServer
operator|.
name|getAttributes
argument_list|(
name|beanName
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"MBeanServerId"
block|,
literal|"SpecificationName"
block|,
literal|"SpecificationVersion"
block|,
literal|"SpecificationVendor"
block|,
literal|"ImplementationName"
block|,
literal|"ImplementationVersion"
block|,
literal|"ImplementationVendor"
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|object
range|:
name|attrs
control|)
block|{
name|Attribute
name|attr
init|=
operator|(
name|Attribute
operator|)
name|object
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|mbeanProps
operator|.
name|put
argument_list|(
name|attr
operator|.
name|getName
argument_list|()
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|je
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Cannot set service properties of Platform MBeanServer service, registering without"
argument_list|,
name|je
argument_list|)
expr_stmt|;
block|}
name|registry
operator|.
name|registerService
argument_list|(
name|MBeanServer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|platformMBeanServer
argument_list|,
name|mbeanProps
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|RepositoryTracker
extends|extends
name|ServiceTracker
argument_list|<
name|Repository
argument_list|,
name|Repository
argument_list|>
block|{
specifier|private
specifier|final
name|SettableFuture
argument_list|<
name|Repository
argument_list|>
name|repoFuture
decl_stmt|;
specifier|private
specifier|final
name|PojoServiceRegistry
name|registry
decl_stmt|;
specifier|private
specifier|final
name|BundleActivator
name|activator
decl_stmt|;
specifier|private
name|RepositoryProxy
name|proxy
decl_stmt|;
specifier|private
specifier|final
name|int
name|timeoutInSecs
decl_stmt|;
specifier|public
name|RepositoryTracker
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|,
name|BundleActivator
name|activator
parameter_list|,
name|SettableFuture
argument_list|<
name|Repository
argument_list|>
name|repoFuture
parameter_list|,
name|int
name|timeoutInSecs
parameter_list|)
block|{
name|super
argument_list|(
name|registry
operator|.
name|getBundleContext
argument_list|()
argument_list|,
name|Repository
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|repoFuture
operator|=
name|repoFuture
expr_stmt|;
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|activator
operator|=
name|activator
expr_stmt|;
name|this
operator|.
name|timeoutInSecs
operator|=
name|timeoutInSecs
expr_stmt|;
name|this
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Repository
name|addingService
parameter_list|(
name|ServiceReference
argument_list|<
name|Repository
argument_list|>
name|reference
parameter_list|)
block|{
name|Repository
name|service
init|=
name|context
operator|.
name|getService
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
block|{
comment|//As its possible that future is accessed before the service
comment|//get registered with tracker. We also capture the initial reference
comment|//and use that for the first access case
name|repoFuture
operator|.
name|set
argument_list|(
name|createProxy
argument_list|(
name|service
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|service
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Repository
name|service
parameter_list|)
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|proxy
operator|.
name|clearInitialReference
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|PojoServiceRegistry
name|getRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
specifier|private
name|Repository
name|createProxy
parameter_list|(
name|Repository
name|service
parameter_list|)
block|{
name|proxy
operator|=
operator|new
name|RepositoryProxy
argument_list|(
name|this
argument_list|,
name|service
argument_list|)
expr_stmt|;
return|return
operator|(
name|Repository
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Repository
operator|.
name|class
block|,
name|JackrabbitRepository
operator|.
name|class
block|,
name|ServiceRegistryProvider
operator|.
name|class
block|}
argument_list|,
name|proxy
argument_list|)
return|;
block|}
specifier|public
name|void
name|shutdownRepository
parameter_list|()
throws|throws
name|BundleException
block|{
try|try
block|{
name|activator
operator|.
name|stop
argument_list|(
name|getRegistry
argument_list|()
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while shutting down activator {}"
argument_list|,
name|activator
operator|.
name|getClass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|shutdown
argument_list|(
name|getRegistry
argument_list|()
argument_list|,
name|timeoutInSecs
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Due to the way SecurityConfiguration is managed in OSGi env its possible      * that repository gets created/shutdown few times. So need to have a proxy      * to access the latest service      */
specifier|private
specifier|static
class|class
name|RepositoryProxy
implements|implements
name|InvocationHandler
block|{
specifier|private
specifier|final
name|RepositoryTracker
name|tracker
decl_stmt|;
specifier|private
name|Repository
name|initialService
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|shutdownInitiated
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|RepositoryProxy
parameter_list|(
name|RepositoryTracker
name|tracker
parameter_list|,
name|Repository
name|initialService
parameter_list|)
block|{
name|this
operator|.
name|tracker
operator|=
name|tracker
expr_stmt|;
name|this
operator|.
name|initialService
operator|=
name|initialService
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Object
name|obj
init|=
name|tracker
operator|.
name|getService
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
name|obj
operator|=
name|initialService
expr_stmt|;
block|}
specifier|final
name|String
name|name
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|//If shutdown then close the framework and return
comment|//Repository would be shutdown by the owning OSGi
comment|//component like RepositoryManager
if|if
condition|(
literal|"shutdown"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|shutdownInitiated
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
name|tracker
operator|.
name|shutdownRepository
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
if|if
condition|(
literal|"getServiceRegistry"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|tracker
operator|.
name|getRegistry
argument_list|()
return|;
block|}
name|checkNotNull
argument_list|(
name|obj
argument_list|,
literal|"Repository service is not available"
argument_list|)
expr_stmt|;
return|return
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
name|args
argument_list|)
return|;
block|}
specifier|public
name|void
name|clearInitialReference
parameter_list|()
block|{
name|this
operator|.
name|initialService
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

