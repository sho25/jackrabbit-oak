begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|solr
operator|.
name|util
package|;
end_package

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
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|CommitPolicy
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|DefaultSolrConfigurationProvider
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|OakSolrConfigurationProvider
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|query
operator|.
name|SolrQueryIndexProvider
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|server
operator|.
name|DefaultSolrServerProvider
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|server
operator|.
name|SolrServerProvider
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
name|query
operator|.
name|QueryIndexProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|BundleReference
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
name|FrameworkUtil
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

begin_comment
comment|/**  * Utilities for Oak Solr integration.  */
end_comment

begin_class
specifier|public
class|class
name|OakSolrUtils
block|{
comment|/**      * adapts the OSGi Solr {@link org.apache.jackrabbit.oak.spi.query.QueryIndexProvider} service      *       * @return a {@link org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndexProvider}      */
specifier|public
specifier|static
name|QueryIndexProvider
name|adaptOsgiQueryIndexProvider
parameter_list|()
block|{
name|QueryIndexProvider
name|queryIndexProvider
init|=
literal|null
decl_stmt|;
try|try
block|{
name|BundleContext
name|ctx
init|=
name|BundleReference
operator|.
name|class
operator|.
name|cast
argument_list|(
name|SolrQueryIndexProvider
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
operator|.
name|getBundle
argument_list|()
operator|.
name|getBundleContext
argument_list|()
decl_stmt|;
name|ServiceReference
name|serviceReference
init|=
name|ctx
operator|.
name|getServiceReference
argument_list|(
name|QueryIndexProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceReference
operator|!=
literal|null
condition|)
block|{
name|queryIndexProvider
operator|=
name|QueryIndexProvider
operator|.
name|class
operator|.
name|cast
argument_list|(
name|ctx
operator|.
name|getService
argument_list|(
name|serviceReference
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
return|return
name|queryIndexProvider
return|;
block|}
comment|/**      * adapt the OSGi Solr {@link SolrServerProvider} service of a given extending class and tries      * to instantiate it if non existing.      *       * @param providerClass      *            the {@link Class} extending {@link SolrServerProvider} to adapt or instantiate      * @param<T>      *            the {@link SolrServerProvider} extension      * @return a {@link SolrServerProvider} adapted from the OSGi service, or a directly      *         instantiated one or<code>null</code> if both failed      */
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|SolrServerProvider
parameter_list|>
name|SolrServerProvider
name|adaptOsgiSolrServerProvider
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|providerClass
parameter_list|)
block|{
name|SolrServerProvider
name|solrServerProvider
init|=
literal|null
decl_stmt|;
try|try
block|{
name|BundleContext
name|ctx
init|=
name|FrameworkUtil
operator|.
name|getBundle
argument_list|(
name|providerClass
argument_list|)
operator|.
name|getBundleContext
argument_list|()
decl_stmt|;
name|ServiceReference
name|serviceReference
init|=
name|ctx
operator|.
name|getServiceReference
argument_list|(
name|SolrServerProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceReference
operator|!=
literal|null
condition|)
block|{
name|solrServerProvider
operator|=
name|SolrServerProvider
operator|.
name|class
operator|.
name|cast
argument_list|(
name|ctx
operator|.
name|getService
argument_list|(
name|serviceReference
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
return|return
name|solrServerProvider
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|SolrServerProvider
parameter_list|>
name|SolrServerProvider
name|getSolrServerProvider
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|providerClass
parameter_list|)
block|{
name|SolrServerProvider
name|solrServerProvider
init|=
name|adaptOsgiSolrServerProvider
argument_list|(
name|providerClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrServerProvider
operator|==
literal|null
operator|&&
name|providerClass
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|solrServerProvider
operator|=
name|providerClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
if|if
condition|(
name|solrServerProvider
operator|==
literal|null
condition|)
block|{
name|solrServerProvider
operator|=
operator|new
name|DefaultSolrServerProvider
argument_list|()
expr_stmt|;
block|}
return|return
name|solrServerProvider
return|;
block|}
comment|/**      * adapt the OSGi Solr {@link org.apache.jackrabbit.oak.plugins.index.solr.configuration.OakSolrConfigurationProvider} service of a given extending class      * and tries to instantiate it if non existing.      *       * @param providerClass      *            the {@link Class} extending {@link org.apache.jackrabbit.oak.plugins.index.solr.configuration.OakSolrConfigurationProvider} to adapt or      *            instantiate      * @param<T>      *            the {@link org.apache.jackrabbit.oak.plugins.index.solr.configuration.OakSolrConfigurationProvider} extension      * @return a {@link org.apache.jackrabbit.oak.plugins.index.solr.configuration.OakSolrConfigurationProvider} adapted from the OSGi service, or a directly      *         instantiated one or<code>null</code> if both failed      */
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|OakSolrConfigurationProvider
parameter_list|>
name|OakSolrConfigurationProvider
name|adaptOsgiOakSolrConfigurationProvider
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|providerClass
parameter_list|)
block|{
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
init|=
literal|null
decl_stmt|;
try|try
block|{
name|BundleContext
name|ctx
init|=
name|FrameworkUtil
operator|.
name|getBundle
argument_list|(
name|providerClass
argument_list|)
operator|.
name|getBundleContext
argument_list|()
decl_stmt|;
name|ServiceReference
name|serviceReference
init|=
name|ctx
operator|.
name|getServiceReference
argument_list|(
name|OakSolrConfigurationProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceReference
operator|!=
literal|null
condition|)
block|{
name|oakSolrConfigurationProvider
operator|=
name|OakSolrConfigurationProvider
operator|.
name|class
operator|.
name|cast
argument_list|(
name|ctx
operator|.
name|getService
argument_list|(
name|serviceReference
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
return|return
name|oakSolrConfigurationProvider
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|OakSolrConfigurationProvider
parameter_list|>
name|OakSolrConfigurationProvider
name|getOakSolrConfigurationProvider
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|providerClass
parameter_list|)
block|{
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
init|=
name|OakSolrUtils
operator|.
name|adaptOsgiOakSolrConfigurationProvider
argument_list|(
name|providerClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakSolrConfigurationProvider
operator|==
literal|null
operator|&&
name|providerClass
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|oakSolrConfigurationProvider
operator|=
name|providerClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
if|if
condition|(
name|oakSolrConfigurationProvider
operator|==
literal|null
condition|)
block|{
name|oakSolrConfigurationProvider
operator|=
operator|new
name|DefaultSolrConfigurationProvider
argument_list|()
expr_stmt|;
block|}
return|return
name|oakSolrConfigurationProvider
return|;
block|}
comment|/**      * Trigger a Solr commit on the basis of the given commit policy (e.g. hard, soft, auto)      *       * @param solrServer      *            the {@link org.apache.solr.client.solrj.SolrServer} used to communicate with the Solr instance      * @param commitPolicy      *            the {@link org.apache.jackrabbit.oak.plugins.index.solr.configuration.CommitPolicy} used to commit changes to a Solr index      * @throws java.io.IOException      *             if any low level IO error occurs      * @throws org.apache.solr.client.solrj.SolrServerException      *             if any error occurs while trying to communicate with the Solr instance      */
specifier|public
specifier|static
name|void
name|commitByPolicy
parameter_list|(
name|SolrServer
name|solrServer
parameter_list|,
name|CommitPolicy
name|commitPolicy
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
switch|switch
condition|(
name|commitPolicy
condition|)
block|{
case|case
name|HARD
case|:
block|{
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|SOFT
case|:
block|{
name|solrServer
operator|.
name|commit
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|AUTO
case|:
block|{
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

