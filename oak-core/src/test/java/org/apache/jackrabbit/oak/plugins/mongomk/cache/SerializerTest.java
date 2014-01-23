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
name|mongomk
operator|.
name|cache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|esotericsoftware
operator|.
name|kryo
operator|.
name|Kryo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|esotericsoftware
operator|.
name|kryo
operator|.
name|io
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|com
operator|.
name|esotericsoftware
operator|.
name|kryo
operator|.
name|io
operator|.
name|Output
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
name|Ordering
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
name|mongomk
operator|.
name|DocumentStore
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
name|mongomk
operator|.
name|MemoryDocumentStore
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
name|mongomk
operator|.
name|NodeDocument
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
name|mongomk
operator|.
name|Revision
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
name|mongomk
operator|.
name|StableRevisionComparator
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
name|assertEquals
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|SerializerTest
block|{
specifier|private
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|revisionSerialization
parameter_list|()
block|{
name|Revision
name|r
init|=
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|r
argument_list|,
name|deserialize
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
argument_list|,
name|deserialize
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeDocSerialization
parameter_list|()
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|NodeDocument
name|doc
init|=
operator|new
name|NodeDocument
argument_list|(
name|store
argument_list|,
name|time
argument_list|)
decl_stmt|;
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|checkSame
argument_list|(
name|doc
argument_list|,
operator|(
name|NodeDocument
operator|)
name|deserialize
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|NodeDocument
argument_list|(
name|store
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"_id"
argument_list|,
literal|"b1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"a2"
argument_list|,
literal|"b2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|checkSame
argument_list|(
name|doc
argument_list|,
operator|(
name|NodeDocument
operator|)
name|deserialize
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|NodeDocument
argument_list|(
name|store
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"_id"
argument_list|,
literal|"b1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"a2"
argument_list|,
name|createRevisionMap
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"a3"
argument_list|,
name|createRevisionMap
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|NodeDocument
name|deserDoc
init|=
operator|(
name|NodeDocument
operator|)
name|deserialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|checkSame
argument_list|(
name|doc
argument_list|,
name|deserDoc
argument_list|)
expr_stmt|;
comment|//Assert that revision keys are sorted
name|NavigableMap
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
name|values
init|=
operator|(
name|NavigableMap
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
operator|)
name|deserDoc
operator|.
name|get
argument_list|(
literal|"a2"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Ordering
operator|.
name|from
argument_list|(
name|StableRevisionComparator
operator|.
name|REVERSE
argument_list|)
operator|.
name|isOrdered
argument_list|(
name|values
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Object
name|deserialize
parameter_list|(
name|Object
name|data
parameter_list|)
block|{
name|Kryo
name|k
init|=
name|KryoFactory
operator|.
name|createInstance
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|Output
name|o
init|=
operator|new
name|Output
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|k
operator|.
name|writeObject
argument_list|(
name|o
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|Input
name|input
init|=
operator|new
name|Input
argument_list|(
name|o
operator|.
name|getBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|o
operator|.
name|position
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|result
init|=
name|k
operator|.
name|readObject
argument_list|(
name|input
argument_list|,
name|data
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Size %d %s %n"
argument_list|,
name|o
operator|.
name|position
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
name|createRevisionMap
parameter_list|()
block|{
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
argument_list|(
name|StableRevisionComparator
operator|.
name|REVERSE
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|i
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"foo"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|private
specifier|static
name|void
name|checkSame
parameter_list|(
name|NodeDocument
name|d1
parameter_list|,
name|NodeDocument
name|d2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|d1
operator|.
name|getCreated
argument_list|()
argument_list|,
name|d2
operator|.
name|getCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|d1
operator|.
name|keySet
argument_list|()
argument_list|,
name|d2
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|d1
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|d1
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|d2
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

