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
name|cli
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
name|Set
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
name|joptsimple
operator|.
name|OptionSpec
import|;
end_import

begin_class
specifier|public
class|class
name|RDBStoreOptions
implements|implements
name|OptionsBean
block|{
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|rdbjdbcuser
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|rdbjdbcpasswd
decl_stmt|;
specifier|private
name|OptionSet
name|options
decl_stmt|;
specifier|public
name|RDBStoreOptions
parameter_list|(
name|OptionParser
name|parser
parameter_list|)
block|{
name|rdbjdbcuser
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"rdbjdbcuser"
argument_list|,
literal|"RDB JDBC user"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|defaultsTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|rdbjdbcpasswd
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"rdbjdbcpasswd"
argument_list|,
literal|"RDB JDBC password"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|defaultsTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|OptionSet
name|options
parameter_list|)
block|{
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|title
parameter_list|()
block|{
return|return
literal|"RDBDocumentStore Options"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|order
parameter_list|()
block|{
return|return
literal|15
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"Options related to configuring RDBDocumentStore for DocumentNodeStore based setups"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|operationNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|rdbjdbcuser
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|rdbjdbcpasswd
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
block|}
end_class

end_unit

