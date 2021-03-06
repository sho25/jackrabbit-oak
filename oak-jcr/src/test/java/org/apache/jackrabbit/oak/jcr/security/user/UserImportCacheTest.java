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
name|jcr
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
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
name|security
operator|.
name|user
operator|.
name|Authorizable
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_comment
comment|/**  * Testing user import with default {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior}  * and pw-history content: test that the history is imported irrespective of the  * configuration.  */
end_comment

begin_class
specifier|public
class|class
name|UserImportCacheTest
extends|extends
name|AbstractImportTest
block|{
annotation|@
name|Override
specifier|protected
name|String
name|getTargetPath
parameter_list|()
block|{
return|return
name|USERPATH
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @since Oak 1.3.4      */
annotation|@
name|Test
specifier|public
name|void
name|testImportUserWithCache
parameter_list|()
throws|throws
name|Exception
block|{
comment|// import user
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"y\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\">"
operator|+
literal|"<sv:value>rep:User</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\">"
operator|+
literal|"<sv:value>41529076-9594-360e-ae48-5922904f345d</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\">"
operator|+
literal|"<sv:value>pw</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\">"
operator|+
literal|"<sv:value>yPrincipal</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:node sv:name=\"rep:cache\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\">"
operator|+
literal|"<sv:value>rep:Cache</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:expiration\" sv:type=\"Long\">"
operator|+
literal|"<sv:value>123456789</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:groupPrincipalNames\" sv:type=\"String\" sv:multiple=\"true\">"
operator|+
literal|"<sv:value>\"testGroup\"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|Authorizable
name|authorizable
init|=
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|Node
name|userNode
init|=
name|getImportSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|userNode
operator|.
name|hasNode
argument_list|(
literal|"rep:cache"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

