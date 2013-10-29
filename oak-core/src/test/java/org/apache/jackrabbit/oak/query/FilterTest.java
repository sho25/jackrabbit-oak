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
name|query
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_SYSTEM
import|;
end_import

begin_import
import|import static
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
import|;
end_import

begin_import
import|import static
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|namepath
operator|.
name|NamePathMapper
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
name|query
operator|.
name|xpath
operator|.
name|XPathToSQL2Converter
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
name|Filter
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
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_class
specifier|public
class|class
name|FilterTest
block|{
specifier|private
specifier|final
name|NodeState
name|types
init|=
name|INITIAL_CONTENT
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SQL2Parser
name|p
init|=
operator|new
name|SQL2Parser
argument_list|(
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|,
name|types
argument_list|)
decl_stmt|;
specifier|private
name|Filter
name|createFilter
parameter_list|(
name|String
name|xpath
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|sql
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
operator|.
name|convert
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|QueryImpl
name|q
init|=
operator|(
name|QueryImpl
operator|)
name|p
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
name|q
operator|.
name|createFilter
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1108"
argument_list|)
specifier|public
name|void
name|mvp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this can refer to a multi-valued property
name|Filter
name|f
init|=
name|createFilter
argument_list|(
literal|"//*[(@prop = 'aaa' and @prop = 'bbb' and @prop = 'ccc')]"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

