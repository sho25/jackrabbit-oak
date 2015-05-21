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
name|security
operator|.
name|authentication
operator|.
name|ldap
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|ldap
operator|.
name|client
operator|.
name|api
operator|.
name|LdapConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|ldap
operator|.
name|client
operator|.
name|api
operator|.
name|LdapConnectionValidator
import|;
end_import

begin_comment
comment|/**  * {@code UnboundConnectionValidator}...  */
end_comment

begin_class
specifier|public
class|class
name|UnboundConnectionValidator
implements|implements
name|LdapConnectionValidator
block|{
comment|/**      * Returns true if<code>connection</code> is connected      *      * @param connection The connection to validate      * @return True, if the connection is still valid      */
annotation|@
name|Override
specifier|public
name|boolean
name|validate
parameter_list|(
name|LdapConnection
name|connection
parameter_list|)
block|{
return|return
name|connection
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
end_class

end_unit

