begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ImmutableSet
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
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|basic
operator|.
name|DefaultSyncConfig
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
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|basic
operator|.
name|DefaultSyncContext
import|;
end_import

begin_comment
comment|/**  * Constants used by the external identity management.  *  * @since Oak 1.5.3  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExternalIdentityConstants
block|{
comment|/**      * Name of the property storing the external identifier.      * This property is of type {@link org.apache.jackrabbit.oak.api.Type#STRING}      * and mandatory for external identities that have been synchronized into      * the repository.      *      * @see DefaultSyncContext#REP_EXTERNAL_ID      */
name|String
name|REP_EXTERNAL_ID
init|=
name|DefaultSyncContext
operator|.
name|REP_EXTERNAL_ID
decl_stmt|;
comment|/**      * Name of the property storing the date of the last synchronization of an      * external identity.      * This property is of type {@link org.apache.jackrabbit.oak.api.Type#DATE}      *      * @see DefaultSyncContext#REP_LAST_SYNCED      */
name|String
name|REP_LAST_SYNCED
init|=
name|DefaultSyncContext
operator|.
name|REP_LAST_SYNCED
decl_stmt|;
comment|/**      * Name of the property storing the principal names of the external groups      * a given external identity (user) is member. Not that the set depends on      * the configured nesting {@link DefaultSyncConfig.User#getMembershipNestingDepth() depth}.      * The existence of this property is optional and will only be created if      * {@link DefaultSyncConfig.User#getDynamicMembership()} is turned on.      *      * This property is of type {@link org.apache.jackrabbit.oak.api.Type#STRINGS}.      * Please note, that for security reasons is system maintained and protected      * on the Oak level and cannot be manipulated by regular {@code ContentSession}      * objects irrespective of the effective permissions.      */
name|String
name|REP_EXTERNAL_PRINCIPAL_NAMES
init|=
literal|"rep:externalPrincipalNames"
decl_stmt|;
comment|/**      * The set of served property names defined by this interface.      */
name|Set
argument_list|<
name|String
argument_list|>
name|RESERVED_PROPERTY_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_EXTERNAL_ID
argument_list|,
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

