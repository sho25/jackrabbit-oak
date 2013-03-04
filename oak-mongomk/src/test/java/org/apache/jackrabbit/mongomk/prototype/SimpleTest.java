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
name|mongomk
operator|.
name|prototype
package|;
end_package

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
name|assertFalse
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
name|assertNull
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|prototype
operator|.
name|DocumentStore
operator|.
name|Collection
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
name|mongomk
operator|.
name|prototype
operator|.
name|Node
operator|.
name|Children
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_comment
comment|/**  * A set of simple tests.  */
end_comment

begin_class
specifier|public
class|class
name|SimpleTest
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|MONGO_DB
init|=
literal|false
decl_stmt|;
comment|//    private static final boolean MONGO_DB = true;
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
operator|new
name|MongoMK
argument_list|()
decl_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathToId
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"0:/"
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|Utils
operator|.
name|getPathFromId
argument_list|(
literal|"0:/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1:/test"
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|Utils
operator|.
name|getPathFromId
argument_list|(
literal|"1:/test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10:/1/2/3/3/4/6/7/8/9/a"
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/1/2/3/3/4/6/7/8/9/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/1/2/3/3/4/6/7/8/9/a"
argument_list|,
name|Utils
operator|.
name|getPathFromId
argument_list|(
literal|"10:/1/2/3/3/4/6/7/8/9/a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathDepth
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Utils
operator|.
name|pathDepth
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Utils
operator|.
name|pathDepth
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Utils
operator|.
name|pathDepth
argument_list|(
literal|"1/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Utils
operator|.
name|pathDepth
argument_list|(
literal|"/a/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Utils
operator|.
name|pathDepth
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|Utils
operator|.
name|pathDepth
argument_list|(
literal|"/a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revision
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Revision
name|r
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// System.out.println(r);
name|Revision
name|r2
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|,
name|r2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|hashCode
argument_list|()
argument_list|,
name|r2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|equals
argument_list|(
name|r2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeGetNode
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
operator|new
name|MongoMK
argument_list|()
decl_stmt|;
name|Revision
name|rev
init|=
name|mk
operator|.
name|newRevision
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
operator|new
name|Node
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
literal|"Hello"
argument_list|)
expr_stmt|;
name|UpdateOp
name|op
init|=
name|n
operator|.
name|asOperation
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|DocumentStore
name|s
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|mk
operator|.
name|getNode
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Hello"
argument_list|,
name|n2
operator|.
name|getProperty
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reAddDeleted
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
name|createMK
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{\"name\": \"Hello\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"test\""
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{\"name\": \"Hallo\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|test
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"name\":\"Hallo\",\":childNodeCount\":0}"
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reAddDeleted2
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
name|createMK
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{\"child\": {}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"test\""
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|test
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":0}"
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|escapePropertyName
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
name|createMK
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{\"name.first\": \"Hello\", \"_id\": \"a\", \"$x\": \"1\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|test
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"$x\":\"1\",\"_id\":\"a\",\"name.first\":\"Hello\",\":childNodeCount\":0}"
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commit
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
name|createMK
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{\"name\": \"Hello\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|test
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"name\":\"Hello\",\":childNodeCount\":0}"
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"a\":{\"name\": \"World\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"b\":{\"name\": \"!\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|test
operator|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Children
name|c
decl_stmt|;
name|c
operator|=
name|mk
operator|.
name|readChildren
argument_list|(
literal|"/"
argument_list|,
literal|"1"
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|rev
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/: [/test]"
argument_list|,
name|c
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|=
name|mk
operator|.
name|readChildren
argument_list|(
literal|"/test"
argument_list|,
literal|"2"
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|rev
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test: [/test/a, /test/b]"
argument_list|,
name|c
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/test\":1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|test
operator|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"test\":1,\"test\":{},\":childNodeCount\":1}"
argument_list|,
name|test
argument_list|)
expr_stmt|;
comment|// System.out.println(test);
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cache
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
name|createMK
argument_list|()
decl_stmt|;
comment|// BAD
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"testRoot\":{} +\"index\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// GOOD
comment|//        String rev = mk.commit("/", "+\"testRoot\":{} ", null, null);
name|String
name|test
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|":id"
argument_list|)
decl_stmt|;
comment|// System.out.println("  " + test);
comment|//        test = mk.getNodes("/testRoot", rev, 0, 0, Integer.MAX_VALUE, ":id");
comment|//        System.out.println("  " + test);
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/testRoot"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//        test = mk.getNodes("/testRoot", rev, 0, 0, Integer.MAX_VALUE, ":id");
comment|//        System.out.println("  " + test);
comment|//        rev = mk.commit("/testRoot/a", "+\"b\":{}", null, null);
comment|//        rev = mk.commit("/testRoot/a/b", "+\"c\":{} +\"d\":{}", null, null);
comment|//        test = mk.getNodes("/testRoot", rev, 0, 0, Integer.MAX_VALUE, ":id");
comment|//        System.out.println("  " + test);
comment|//        test = mk.getNodes("/", rev, 0, 0, Integer.MAX_VALUE, ":id");
comment|//        System.out.println("  " + test);
comment|//        rev = mk.commit("/index", "+\"a\":{}", null, null);
name|test
operator|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|":id"
argument_list|)
expr_stmt|;
comment|// System.out.println("  " + test);
comment|//        test = mk.getNodes("/testRoot", rev, 0, 0, Integer.MAX_VALUE, ":id");
comment|//        System.out.println("  " + test);
comment|//        assertEquals("{\"name\":\"Hello\",\":childNodeCount\":0}", test);
comment|//
comment|//        rev = mk.commit("/test", "+\"a\":{\"name\": \"World\"}", null, null);
comment|//        rev = mk.commit("/test", "+\"b\":{\"name\": \"!\"}", null, null);
comment|//        test = mk.getNodes("/test", rev, 0, 0, Integer.MAX_VALUE, null);
comment|//        Children c;
comment|//        c = mk.readChildren("/", "1",
comment|//                Revision.fromString(rev), Integer.MAX_VALUE);
comment|//        assertEquals("/: [/test]", c.toString());
comment|//        c = mk.readChildren("/test", "2",
comment|//                Revision.fromString(rev), Integer.MAX_VALUE);
comment|//        assertEquals("/test: [/test/a, /test/b]", c.toString());
comment|//
comment|//        rev = mk.commit("", "^\"/test\":1", null, null);
comment|//        test = mk.getNodes("/", rev, 0, 0, Integer.MAX_VALUE, null);
comment|//        assertEquals("{\"test\":1,\"test\":{},\":childNodeCount\":1}", test);
comment|// System.out.println(test);
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeletion
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
name|createMK
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"testDel\":{\"name\": \"Hello\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/testDel"
argument_list|,
literal|"+\"a\":{\"name\": \"World\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/testDel"
argument_list|,
literal|"+\"b\":{\"name\": \"!\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/testDel"
argument_list|,
literal|"+\"c\":{\"name\": \"!\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Children
name|c
init|=
name|mk
operator|.
name|readChildren
argument_list|(
literal|"/testDel"
argument_list|,
literal|"1"
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|rev
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|c
operator|.
name|children
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/testDel"
argument_list|,
literal|"-\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|c
operator|=
name|mk
operator|.
name|readChildren
argument_list|(
literal|"/testDel"
argument_list|,
literal|"2"
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|rev
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|c
operator|.
name|children
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"testDel\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|mk
operator|.
name|getNode
argument_list|(
literal|"/testDel"
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|rev
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddAndMove
parameter_list|()
block|{
name|MongoMK
name|mk
init|=
name|createMK
argument_list|()
decl_stmt|;
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/root\":{}\n"
operator|+
literal|"+\"/root/a\":{}\n"
operator|+
literal|"+\"/root/a/b\":{}\n"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|">\"/root/a\":\"/root/c\"\n"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/root/a"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/root/c/b"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|MongoMK
name|createMK
parameter_list|()
block|{
if|if
condition|(
name|MONGO_DB
condition|)
block|{
name|DB
name|db
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|db
argument_list|)
expr_stmt|;
return|return
operator|new
name|MongoMK
argument_list|(
name|db
argument_list|,
literal|0
argument_list|)
return|;
block|}
return|return
operator|new
name|MongoMK
argument_list|()
return|;
block|}
block|}
end_class

end_unit

