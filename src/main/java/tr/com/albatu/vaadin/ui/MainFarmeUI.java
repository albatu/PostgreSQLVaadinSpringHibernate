package tr.com.albatu.vaadin.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.grid.editor.Editor;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import tr.com.albatu.dal.CategoryDal;
import tr.com.albatu.entities.Category;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import com.vaadin.flow.component.combobox.ComboBox;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;

@Route("")

public class MainFarmeUI extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8638542248934979758L;
	@Autowired
	private CategoryDal categoryDal;

	public MainFarmeUI(CategoryDal categoryDal) {

		this.categoryDal = categoryDal;

		Button insertRowButton = new Button("Yeni Kayıt");
		Grid<Category> grid = new Grid<>(Category.class, false);
		Editor<Category> editor = grid.getEditor();
		List<Category> list = new ArrayList<Category>();
		categoryDal.findAll().forEach(p -> list.add(p));

		ListDataProvider<Category> getList = new ListDataProvider<Category>(list);
		grid.setItems(getList);
		Grid.Column<Category> idColumn = grid.addColumn(Category::getId).setHeader("#").setAutoWidth(true)
				.setFlexGrow(0);
		Grid.Column<Category> categoryNameColumn = grid.addColumn(Category::getName).setHeader("Kategori Adı")
				.setAutoWidth(true).setFlexGrow(0);

		// var findBy = categoryDal.findById(0);
		Grid.Column<Category> parentCategoryColumn = grid
				.addColumn(p -> categorName(categoryDal.findById(p.getParentId()).orElse(null)))
				.setHeader("Üst Kategori Adı").setAutoWidth(true).setFlexGrow(0);

		Grid.Column<Category> editColumn = grid.addComponentColumn(Category -> {
			Button editButton = new Button("Edit");
			editButton.addClickListener(e -> {
				if (editor.isOpen())
					editor.cancel();
				
				grid.getEditor().editItem(Category);
			});
			editButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_ERROR);
			return editButton;
		}).setWidth("405px").setFlexGrow(0);

		Binder<Category> binder = new Binder<>(Category.class);
		editor.setBinder(binder);
		editor.setBuffered(true);
		TextField categoryNameField = new TextField();
		IntegerField idField = new IntegerField();

		
		ComboBox<Category> comboBox = new ComboBox<>();
		List<Category> categories = new ArrayList<Category>();
		// ComboBoxListDataView<Category> comboList;
		categoryDal.findAll().forEach(p -> categories.add(p));
		Collection<Category> comboList = categories.stream().filter(p -> p.getParentId() == 0).toList();

		comboBox.setItems(comboList);
		comboBox.setItemLabelGenerator(Category::getName);
		comboBox.setSizeFull();
		categoryNameField.setWidthFull();
		binder.forField(categoryNameField).asRequired("Kategori boş bırakılamaz")
				// .withStatusLabel(categoryNameLabel)
				.bind(Category::getName, Category::setName);

		// binder.bind(categories,Category::getParentId, Category::setParentId);
		//binder.bindInstanceFields(comboBox);
		var category = new Category();
		//comboBox.setItemsWithFilterConverter(Category::getParentId, Category::setParentId);
		binder.forField(comboBox)
        .withConverter(new Converter<Category, Integer>() {
            @Override
            public Result<Integer> convertToModel(Category value, ValueContext context) {
                return Result.ok(value == null ? null : value.getId());
            }

            @Override
            public Category convertToPresentation(Integer value, ValueContext context) {
                return categoryDal.findById(value).orElse(null);
            }
        })
        .bind(Category::getParentId, Category::setParentId);
		parentCategoryColumn.setEditorComponent(comboBox);
		categoryNameColumn.setEditorComponent(categoryNameField);
		binder.forField(idField).bind(Category::getId, Category::setId);

		// binder.forField(comboBox).bind(Category::getName,Category::setName);
		// binder.forField(comboBox).withConverter((v -> extractID(v)), v ->
		// findTuple(service, v).bind("comboBox");

		insertRowButton.addClickListener(e -> {
			if (editor.isOpen())
				editor.cancel();
			var newItem = new Category();
			newItem.setId(0);
			newItem.setName(null);
			newItem.setParentId(0);
			getList.getItems().add(newItem);
			getList.refreshAll();
			grid.getEditor().editItem(newItem);

		});

		Button saveButton = new Button("Save", e -> editor.save());
		Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
		cancelButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
			
			@Override
			public void onComponentEvent(ClickEvent<Button> event) {
				
				
				List<Category> list = new ArrayList<Category>();
				categoryDal.findAll().forEach(p -> list.add(p));

				ListDataProvider<Category> getList = new ListDataProvider<Category>(list);
				grid.setItems(getList);
				grid.getDataProvider().refreshAll();
			}
		});
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
		actions.setPadding(false);
		editColumn.setEditorComponent(actions);

		saveButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onComponentEvent(ClickEvent<Button> event) {

				var data = new Category();
				data.setId(idField.getValue());
				data.setName(categoryNameField.getValue());
				categoryDal.save(data);

				var status = new Span();
				status.setVisible(false);

				ConfirmDialog dialog = new ConfirmDialog();
				dialog.setHeader("Ekleme işlemi");
				dialog.setText("Ekleme işlemi başarılı bir şekilde gerçekleştirilmiştir.");
				dialog.open();
				status.setVisible(false);
				dialog.setCancelable(true);
				// dialog.addCancelListener(event -> setStatus("Canceled"));

				dialog.setConfirmText("Tamam");
				dialog.setConfirmButtonTheme("primary");
				// dialog.addConfirmListener(event -> setStatus("Deleted"));

				if (editor.isOpen())
					editor.cancel();
					

				List<Category> list = new ArrayList<Category>();
				categoryDal.findAll().forEach(p -> list.add(p));

				ListDataProvider<Category> getList = new ListDataProvider<Category>(list);
				grid.setItems(getList);
				grid.getDataProvider().refreshAll();

				add(dialog);

			}
		});

		grid.getPageSize();
		add(insertRowButton, grid);

	}

	String categorName(Category category) {
		if (category == null) {
			return "Üst kategori yok";
		}

		else {

			return category.getName();
		}
	}
	
	

}
