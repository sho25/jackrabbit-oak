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
name|segment
package|;
end_package

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
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicy
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
name|Service
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
name|commons
operator|.
name|SimpleValueFactory
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
name|api
operator|.
name|Descriptors
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
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
comment|/**  * The SegmentDiscoveryLiteService is taking care of providing a repository  * descriptor that contains the current cluster-view details.  *<p>  * Note that since currently the SegmentNodeStore is not cluster-based,  * the provided clusterView is a hard-coded one consisting only of the  * local instance. But it is nevertheless useful for upper layer discovery.oak.  *<p>  * @see DocumentDiscoveryLiteService for a more in-depth description of the discovery-lite descriptor  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|immediate
operator|=
literal|true
argument_list|)
annotation|@
name|Service
argument_list|(
name|value
operator|=
block|{
name|SegmentDiscoveryLiteService
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|SegmentDiscoveryLiteService
block|{
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"org.apache.jackrabbit.oak.plugins.segment.SegmentDiscoveryLiteService"
decl_stmt|;
comment|/**      * Name of the repository descriptor via which the clusterView is published      * - which is the raison d'etre of the DocumentDiscoveryLiteService      * TODO: move this constant to a generic place for both segment and document      **/
specifier|public
specifier|static
specifier|final
name|String
name|OAK_DISCOVERYLITE_CLUSTERVIEW
init|=
literal|"oak.discoverylite.clusterview"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SegmentDiscoveryLiteService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** This provides the 'clusterView' repository descriptors **/
specifier|private
class|class
name|DiscoveryLiteDescriptor
implements|implements
name|Descriptors
block|{
specifier|final
name|SimpleValueFactory
name|factory
init|=
operator|new
name|SimpleValueFactory
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getKeys
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|OAK_DISCOVERYLITE_CLUSTERVIEW
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStandardDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
operator|!
name|OAK_DISCOVERYLITE_CLUSTERVIEW
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSingleValueDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
operator|!
name|OAK_DISCOVERYLITE_CLUSTERVIEW
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|getValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
operator|!
name|OAK_DISCOVERYLITE_CLUSTERVIEW
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|factory
operator|.
name|createValue
argument_list|(
name|getClusterViewAsDescriptorValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getValues
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
operator|!
name|OAK_DISCOVERYLITE_CLUSTERVIEW
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Value
index|[]
block|{
name|getValue
argument_list|(
name|key
argument_list|)
block|}
return|;
block|}
block|}
comment|/**      * Require a static reference to the NodeStore. Note that this implies the      * service is only active for segmentNS (which is the idea)      **/
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|MANDATORY_UNARY
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|STATIC
argument_list|)
specifier|private
specifier|volatile
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|String
name|runtimeClusterId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/**      * returns the clusterView as a json value for it to be provided via the      * repository descriptor      **/
specifier|private
name|String
name|getClusterViewAsDescriptorValue
parameter_list|()
block|{
comment|// since currently segment node store is not running in a cluster
comment|// we can hard-code a single-vm descriptor here:
comment|// {"seq":4,"final":true,"me":1,"active":[1],"deactivating":[],"inactive":[2]}
comment|// OAK-3672 : 'id' is now allowed to be null (supported by upper layers),
comment|//            and for tarMk we're doing exactly that (id==null) - indicating
comment|//            to upper layers that we're not really in a cluster and that
comment|//            this low level descriptor doesn't manage the 'cluster id'
comment|//            in such a case.
return|return
literal|"{\"seq\":1,\"final\":true,\"me\":1,\"active\":[1],\"deactivating\":[],\"inactive\":[]}"
return|;
block|}
comment|/**      * On activate the SegmentDiscoveryLiteService registers       * the descriptor      */
annotation|@
name|Activate
specifier|public
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"activate: start"
argument_list|)
expr_stmt|;
comment|//TODO: i have a feeling this could be done nicer
comment|// but doing a reference to the ProxyNodeStore wouldn't be accurate enough
comment|// and since the SegmentNodeStore is not directly registered with osgi
comment|// can't refer to that - so that's why there's currently this ugly fallback
specifier|final
name|boolean
name|weAreOnSegment
decl_stmt|;
if|if
condition|(
name|nodeStore
operator|instanceof
name|SegmentNodeStore
condition|)
block|{
comment|// this would currently never happen - but would be straight forward,
comment|// so support it
name|weAreOnSegment
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeStore
operator|instanceof
name|SegmentNodeStoreService
condition|)
block|{
comment|// this is the normal case for tarMk
comment|// could also test if it's a ProxyNodeStore - but
comment|// that one doesn't currently allow access to the delegate
comment|// so go directly to SegmentNodeStoreService
name|weAreOnSegment
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// this is the case for DocumentNodeStore for example
name|weAreOnSegment
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|weAreOnSegment
condition|)
block|{
comment|// then disable the SegmentDiscoveryLiteService
name|logger
operator|.
name|info
argument_list|(
literal|"activate: nodeStore is not a SegmentNodeStore, thus disabling: "
operator|+
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|context
operator|.
name|disableComponent
argument_list|(
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// register the Descriptors - for Oak to pass it upwards
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|OsgiWhiteboard
name|whiteboard
init|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
operator|.
name|getBundleContext
argument_list|()
argument_list|)
decl_stmt|;
name|whiteboard
operator|.
name|register
argument_list|(
name|Descriptors
operator|.
name|class
argument_list|,
operator|new
name|DiscoveryLiteDescriptor
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"activate: end"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

