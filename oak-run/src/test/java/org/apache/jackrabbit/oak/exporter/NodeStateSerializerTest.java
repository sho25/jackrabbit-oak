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
name|exporter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
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
name|Type
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
name|json
operator|.
name|BlobDeserializer
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
name|json
operator|.
name|JsonDeserializer
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
name|EqualsDiff
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
name|NodeBuilder
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
name|Rule
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

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
name|Charsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStateSerializerTest
block|{
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|BlobDeserializer
name|blobHandler
init|=
name|mock
argument_list|(
name|BlobDeserializer
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|basics
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeStateSerializer
name|serializer
init|=
operator|new
name|NodeStateSerializer
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|serializer
operator|.
name|serialize
argument_list|()
decl_stmt|;
name|NodeState
name|nodeState2
init|=
name|deserialize
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|nodeState2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|serializeToFile
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeStateSerializer
name|serializer
init|=
operator|new
name|NodeStateSerializer
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|json
init|=
operator|new
name|File
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|,
name|serializer
operator|.
name|getFileName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|json
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|text
init|=
name|Files
operator|.
name|toString
argument_list|(
name|json
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|NodeState
name|nodeState2
init|=
name|deserialize
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|nodeState2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|text
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"d"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"d"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"d"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo3"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeStateSerializer
name|serializer
init|=
operator|new
name|NodeStateSerializer
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|setFormat
argument_list|(
name|NodeStateSerializer
operator|.
name|Format
operator|.
name|TXT
argument_list|)
expr_stmt|;
name|String
name|txt
init|=
name|serializer
operator|.
name|serialize
argument_list|()
decl_stmt|;
comment|//System.out.println(txt);
block|}
specifier|private
name|NodeState
name|deserialize
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|JsonDeserializer
name|deserializer
init|=
operator|new
name|JsonDeserializer
argument_list|(
name|blobHandler
argument_list|)
decl_stmt|;
return|return
name|deserializer
operator|.
name|deserialize
argument_list|(
name|json
argument_list|)
return|;
block|}
block|}
end_class

end_unit

