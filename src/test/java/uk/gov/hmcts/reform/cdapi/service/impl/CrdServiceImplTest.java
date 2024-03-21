package uk.gov.hmcts.reform.cdapi.service.impl;

class CrdServiceImplTest {
/*
    @InjectMocks
    CrdServiceImpl crdServiceImpl;

    @Spy
    ListOfValuesRepository listOfValuesRepository;

    @Test
    void retrieveCategoriesByCategoryId() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingChannel", null,
                                                                         null, null, null));
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  null, null,
                                                       null,null, null);
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        Category actualCategory = result.get(0);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), actualCategory.getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), actualCategory.getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getServiceId(), actualCategory.getServiceId());
        assertEquals(listOfValueDtos.get(0).getActive(), actualCategory.getActiveFlag());
        assertEquals(listOfValueDtos.get(0).getParentCategory(), actualCategory.getParentCategory());
        assertEquals(listOfValueDtos.get(0).getParentKey(), actualCategory.getParentKey());
        assertEquals(listOfValueDtos.get(0).getValueCy(), actualCategory.getValueCy());
        assertEquals(listOfValueDtos.get(0).getValueEn(), actualCategory.getValueEn());
        assertEquals(listOfValueDtos.get(0).getHintTextCy(), actualCategory.getHintTextCy());
        assertEquals(listOfValueDtos.get(0).getHintTextEn(), actualCategory.getHintTextEn());
        assertEquals(listOfValueDtos.get(0).getLovOrder(), actualCategory.getLovOrder());
        assertNull(actualCategory.getChildNodes());
    }

    @Test
    void retrieveCategoriesByAllParams() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtos();
        ListOfValueDto inactiveCategory = CrdTestSupport.createListOfCategoriesDtoMock("HearingChannel",
                                            "BBA3", null, null, "telephone");
        inactiveCategory.setActive("n");
        listOfValueDtos.add(inactiveCategory);

        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel", "BBA3", "telephone",
                                           "HearingChannel", "telephone","y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals("y", result.get(0).getActiveFlag());
        assertEquals("n", result.get(1).getActiveFlag());
        assertEquals("y", result.get(2).getActiveFlag());
    }



    @NotNull
    private List<ListOfValueDto> buildListOfValuesDtos() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingChannel", "BBA3",
                                                                            null,
                                                                            null, "telephone"));
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingSubChannel", "BBA3",
                                                                            "HearingChannel",
                                                                            "telephone", "telephone"));
        return listOfValueDtos;
    }

    @Test
    void retrieveCategoriessByCategoryIdWithChildNodes() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtos();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  null, null,
                                                       null,null, "y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getKey(),
                     result.get(0).getChildNodes().get(0).getKey());
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getCategoryKey(), result.get(0).getChildNodes().get(0)
            .getCategoryKey());
    }

    @Test
    void retrieveCategoriesByCategoryIdWithNoChildNodes() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtos();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingSubChannel",  null, null,
                                           "HearingChannel","telephone", "y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());
        assertNull(result.get(0).getChildNodes());
    }

    @Test
    void retrieveCategoriesByCategoryIdWithParentCategory() {
        verifyWithParams("y", "HearingChannel", null);
        verifyWithParams("y", null, "telephone");
        verifyWithParams("n", "HearingChannel", null);
    }

    private void verifyWithParams(String isChildRequired, String parentCategory, String parentKey) {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtos();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingSubChannel", null, null,
                                                       parentCategory, parentKey, isChildRequired);
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());
    }

    @Test
    void retrieveCategoriesByCategoryIdWithEmptyParams() {
        List<ListOfValueDto> listOfValueDtos = List.of(CrdTestSupport.createListOfCategoriesDtoMock(
            "HearingChannel", "", "", "", ""));

        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  "", "",
                                                       null,"", "");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());

    }

    @Test
    void shouldThrowNotFoundExceptionIfListEmptyForCategoryWithUnMappedParams() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();

        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  null, null,
                                                       null,null, "n");
        assertThrows(ResourceNotFoundException.class, () ->
                         crdServiceImpl.retrieveListOfValuesByCategory(request),
                     "Data not found"
        );

    }

    @Test
    void retrieveCategoriesByServiceIdWithNoChildNodes() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtos();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  "BBA3", null,
                                                       null,null, "n");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getServiceId(), result.get(0).getServiceId());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());
        assertNull(result.get(0).getChildNodes());
    }

    @Test
    void retrieveCategoriesByServiceIdWithChildNodes() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtos();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  "BBA3", null,
                                                       null,null, "y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getServiceId(), result.get(0).getServiceId());
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getKey(),
                     result.get(0).getChildNodes().get(0).getKey());
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getCategoryKey(), result.get(0).getChildNodes().get(0)
            .getCategoryKey());
    }

    @NotNull
    private List<ListOfValueDto> buildListOfValuesDtosWithListOfValuesOrders() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingChannel", "BBA3",
                                                                         null, null,
                                                                         "telephone", 1L));
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingSubChannel", "BBA3",
                                                                         "HearingChannel",
                                                                         "telephone", "telephone", 3L));
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingSubChannel", "BBA3",
                                                                         "HearingChannel",
                                                                         "telephone", "telephone", 2L));
        return listOfValueDtos;
    }

    @Test
    void retrieveCategoriesByServiceIdWithListOfValuesOrders() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtosWithListOfValuesOrders();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  "BBA3", null,
                                                       null,null, "n");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getServiceId(), result.get(0).getServiceId());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActiveFlag());
        assertNull(result.get(0).getChildNodes());
        assertEquals(listOfValueDtos.get(0).getLovOrder(), result.get(0).getLovOrder());
        assertEquals(listOfValueDtos.get(2).getLovOrder(), result.get(1).getLovOrder());
        assertEquals(listOfValueDtos.get(1).getLovOrder(), result.get(2).getLovOrder());
    }

    @NotNull
    private List<ListOfValueDto> buildListOfValuesDtosWithDefaultOrdering() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();
        final var listOfValueMock1 = CrdTestSupport.createListOfCategoriesDtoMock("HearingChannel", "BBA3",
                                                                                         null, null,
                                                                                         "telephone"
        );
        listOfValueMock1.setValueEn("third");
        listOfValueDtos.add(listOfValueMock1);

        final var listOfValueMock2 = CrdTestSupport.createListOfCategoriesDtoMock("HearingSubChannel", "BBA3",
                                                                                         "HearingChannel",
                                                                                         "telephone", "telephone"
        );
        listOfValueMock2.setValueEn("first");
        listOfValueDtos.add(listOfValueMock2);
        final var listOfValueMock3 = CrdTestSupport.createListOfCategoriesDtoMock("HearingSubChannel", "BBA3",
                                                                                         "HearingChannel",
                                                                                         "telephone", "telephone"
        );
        listOfValueMock3.setValueEn("second");
        listOfValueDtos.add(listOfValueMock3);
        return listOfValueDtos;
    }

    @Test
    void retrieveCategoriesByServiceIdWithDefaultOrdering() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtosWithDefaultOrdering();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  "BBA3", null,
                                                       null,null, "n");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getServiceId(), result.get(1).getServiceId());
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getCategoryKey(), result.get(1).getCategoryKey());
        assertEquals(listOfValueDtos.get(1).getActive(), result.get(1).getActiveFlag());
        assertNull(result.get(1).getChildNodes());
        assertEquals(listOfValueDtos.get(0).getLovOrder(), result.get(2).getLovOrder());
        assertEquals(listOfValueDtos.get(1).getLovOrder(), result.get(0).getLovOrder());
        assertEquals(listOfValueDtos.get(2).getLovOrder(), result.get(1).getLovOrder());
    }

    @Test
    void retrieveCategoriesByServiceIdWithDefaultOrderingThrowsNullPointerExceptionForMissingDefault() {
        List<ListOfValueDto> listOfValueDtos = buildListOfValuesDtosWithDefaultOrdering();

        listOfValueDtos.get(1).setValueEn(null);

        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());

        CategoryRequest request = buildCategoryRequest("HearingChannel",  "BBA3", null,
                                                       null,null, "n");

        final var nullPointerException = assertThrows(NullPointerException.class, () ->
                                                          crdServiceImpl.retrieveListOfValuesByCategory(request),
                                                      "Expected NPE to be thrown"
        );

        assertNotNull(nullPointerException);
    }

    @Test
    @SuppressWarnings("unchecked")
    void retrieveCategoriesByCategoryNonExisting() {

        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();
        doReturn(listOfValueDtos).when(listOfValuesRepository)
            .findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any());
        CategoryRequest request = buildCategoryRequest("XXXXX",  "BBA3", null,
                                                       null,null, "n");
        final var dataNotFoundException = assertThrows(ResourceNotFoundException.class, () ->
                                                          crdServiceImpl.retrieveListOfValuesByCategory(request),
                                                      "Data not found"
        );
        assertNotNull(dataNotFoundException);
        assertEquals("Data not found", dataNotFoundException.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void retrieveCategoriesByServiceIdsNonExisting() {
        CategoryRequest request = buildCategoryRequest("HearingChannel", "XXXXX", "telephone",
                                                       null, null,"y");
        List<ListOfValueDto> listOfValueDtos = List.of(CrdTestSupport.createListOfCategoriesDtoMock(
            "HearingChannel", "", null, null, "telephone"));

        when(listOfValuesRepository.findAll(any(Specification.class))).thenReturn(listOfValueDtos);

        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals("y", result.get(0).getActiveFlag());
    }

    @Test
    @SuppressWarnings("unchecked")
    void retrieveCategoriesCheckServiceIdsExist() {

        Specification<ListOfValueDto> query = null;
        CategoryRequest request = buildCategoryRequest("HearingChannel", "XXXXX", "telephone",
                                                       null, null,"y");

        doReturn(Collections.emptyList()).when(listOfValuesRepository).findAll(query);
        List<ListOfValueDto> result = crdServiceImpl.checkServiceIdExists
            (request,any(Specification.class),true);

        assertNotNull(result);
        assertEquals(0,result.size());

    }
*/
}
