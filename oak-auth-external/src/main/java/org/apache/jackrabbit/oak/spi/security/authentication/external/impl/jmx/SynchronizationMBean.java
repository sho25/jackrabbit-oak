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
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * Provides utilities to manage synchronized external identities.  * The operations return a single or array of messages of the operations performed. for simplicity the messages are  * JSON serialized strings:  *<xmp>  * {  *     "op": "upd",  *     "uid": "bob",  *     "eid": "cn=bob,o=apache"  * }  *</xmp>  *  * With the following operations:  *<ul>  *<li>nop: nothing changed</li>  *<li>upd: entry updated</li>  *<li>add: entry added</li>  *<li>del: entry deleted</li>  *<li>err: operation failed. in this case, the 'msg' property will contain a reason</li>  *</ul>  *  * Note that this interface is not exported via OSGi as it is not intended to use outside of JMX (yet).  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|SynchronizationMBean
block|{
comment|/**      * Returns the name of the {@link org.apache.jackrabbit.oak.spi.security.authentication.external.SyncHandler}      * that this MBean operates on.      *      * @return the name of the sync handler.      */
annotation|@
name|Nonnull
name|String
name|getSyncHandlerName
parameter_list|()
function_decl|;
comment|/**      * Returns the name of the {@link org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider}      * that this MBean operates on.      *      * @return the name of the IDP.      */
annotation|@
name|Nonnull
name|String
name|getIDPName
parameter_list|()
function_decl|;
comment|/**      * Synchronizes the local users with the given user ids.      * @param userIds the user ids      * @param purge if {@code true} users that don't exist in the IDP are deleted.      * @return result messages.      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncUsers
parameter_list|(
annotation|@
name|Nonnull
name|String
index|[]
name|userIds
parameter_list|,
name|boolean
name|purge
parameter_list|)
function_decl|;
comment|/**      * Synchronizes all local users with the given user ids. Note that this can be an expensive operation since all      * potential users need to be examined.      *      * @param purge if {@code true} users that don't exist in the IDP are deleted.      * @return result messages.      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncAllUsers
parameter_list|(
name|boolean
name|purge
parameter_list|)
function_decl|;
comment|/**      * Synchronizes the external users with the given external ids.      * @param externalIds the external id      * @return result messages.      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncExternalUsers
parameter_list|(
annotation|@
name|Nonnull
name|String
index|[]
name|externalIds
parameter_list|)
function_decl|;
comment|/**      * Synchronizes all the external users, i.e. basically imports the entire IDP. Note that this can be an expensive      * operation.      *      * @return result messages.      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncAllExternalUsers
parameter_list|()
function_decl|;
comment|/**      * Returns a list of orphaned users, i.e. users that don't exist anymore on the IDP. Note that this can be an      * expensive operation since all potential users need to be examined.      * @return a list of the user ids of orphaned users.      */
annotation|@
name|Nonnull
name|String
index|[]
name|listOrphanedUsers
parameter_list|()
function_decl|;
comment|/**      * Purges all orphaned users. this is similar to invoke {@link #syncUsers(String[], boolean)} with the list of      * orphaned users. Note tha this can eb an expensive operation since all potential users need to be examined.      *      * @return result messages.      */
annotation|@
name|Nonnull
name|String
index|[]
name|purgeOrphanedUsers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

