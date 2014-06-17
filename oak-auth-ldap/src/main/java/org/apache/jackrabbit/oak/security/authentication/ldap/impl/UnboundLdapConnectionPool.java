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
name|commons
operator|.
name|pool
operator|.
name|impl
operator|.
name|GenericObjectPool
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
name|LdapConnection
import|;
end_import

begin_comment
comment|/**  * A pool implementation for LdapConnection objects.  *<p>  * This class is just a wrapper around the commons GenericObjectPool, and has  * a more meaningful name to represent the pool type.  */
end_comment

begin_class
specifier|public
class|class
name|UnboundLdapConnectionPool
extends|extends
name|GenericObjectPool
argument_list|<
name|LdapConnection
argument_list|>
block|{
comment|/**      * Instantiates a new LDAP connection pool.      *      * @param factory the LDAP connection factory      */
specifier|public
name|UnboundLdapConnectionPool
parameter_list|(
name|PoolableUnboundConnectionFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gives a Unbound LdapConnection fetched from the pool.      *      * @return an LdapConnection object from pool      * @throws Exception if an error occurs while obtaining a connection from the factory      */
specifier|public
name|LdapConnection
name|getConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|super
operator|.
name|borrowObject
argument_list|()
return|;
block|}
comment|/**      * Places the given LdapConnection back in the pool.      *      * @param connection the LdapConnection to be released      * @throws Exception if an error occurs while releasing the connection      */
specifier|public
name|void
name|releaseConnection
parameter_list|(
name|LdapConnection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|returnObject
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

