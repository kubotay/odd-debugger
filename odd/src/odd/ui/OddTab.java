package odd.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import odd.core.IOddCoreConstants;


public class OddTab extends AbstractLaunchConfigurationTab {

	public static String OBJECTDIAGRAM_TAB_NAME="ObjectDiagram";

	public static final String ODD_MODE = "odd";

	protected Set<String> oddModes;

	protected Button enableOddButton;

    protected Text addFilterText;

    protected Button addFilterButton;

    protected Button removeFilterButton;

    protected org.eclipse.swt.widgets.List filtersList;

    public OddTab() {
        oddModes = new HashSet<>();
        oddModes.add(ODD_MODE);
    }

    @Override
	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new GridLayout(1, true));
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.horizontalSpan = 1;
        c.setLayoutData(layoutData);

        // Control for enabling Odd
        enableOddButton = new Button(c, SWT.CHECK);
        enableOddButton.setText("Enable debugging with Object Diagram.");
        enableOddButton.setLayoutData(new GridData());
        enableOddButton.setSelection(false);
        enableOddButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {

            }
        });

        Composite c1 = new Composite(c, SWT.NONE);
        c1.setLayout(new GridLayout(2, true));
        GridData layoutData1 = new GridData(GridData.FILL_BOTH);
        layoutData1.horizontalSpan = 1;
        c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        // Control for entering exclusion filters
        addFilterText = new Text(c1, SWT.BORDER);
        addFilterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        addFilterText.setToolTipText("Enter Exclusion Filter");
        addFilterText.setText("Enter Exclusion Filter");
        addFilterText.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                String data = addFilterText.getText();
                addFilterButton.setEnabled(data != null && !data.trim().isEmpty());
            }
        });
        addFilterText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if(addFilterText.getText().trim().equals("Enter Exclusion Filter")){
                    addFilterText.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if(addFilterText.getText().trim().isEmpty()){
                    addFilterText.setText("Enter Exclusion Filter");
                }
            }
        });

        addFilterButton = new Button(c1, SWT.NONE);
        addFilterButton.setText("Add Filter");
        addFilterButton.setEnabled(false);
        addFilterButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                addFilter();
                addFilterButton.setEnabled(false);
                setDirty(true);
                updateLaunchConfigurationDialog();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {

            }
        });

        filtersList = new org.eclipse.swt.widgets.List(c1, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        filtersList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        filtersList.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                removeFilterButton.setEnabled(filtersList.getSelectionCount() > 0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {

            }
        });

        removeFilterButton = new Button(c1, SWT.NONE);
        removeFilterButton.setText("Remove Filter");
        removeFilterButton.setEnabled(false);
        removeFilterButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                removeFilters();
                addFilterText.setText("Enter Exclusion Filter");
                removeFilterButton.setEnabled(false);
                setDirty(true);
                updateLaunchConfigurationDialog();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {

            }
        });
        setControl(c);
        setDirty(false);

	}

    private void removeFilters(){
        int[] selectedFilters = filtersList.getSelectionIndices();
        filtersList.remove(selectedFilters);
    }
    private void addFilter() {
        String newFilter = addFilterText.getText().trim();
        if(!newFilter.isEmpty()){
            if(validateFilter(newFilter)){
                for (String filter : filtersList.getItems()) {
                    if(newFilter.equals(filter))
                        return;
                }
            }
            filtersList.add(newFilter);
            addFilterText.setText("Enter Exclusion Filter");
        } else {
            addFilterText.setText("Enter Exclusion Filter");
        }
    }

    private boolean validateFilter(String filter){
        return !filter.contains(",");
    }
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
        Set<String> modes = null;
        try {
            modes = configuration.getAttribute(LaunchConfiguration.ATTR_LAUNCH_MODES, Collections.EMPTY_SET);
            if(modes.isEmpty()){
                enableOddButton.setSelection(false);
            } else {
                enableOddButton.setSelection(modes.containsAll(oddModes));
            }
            List<String> exclusionFilters = configuration.getAttribute(IOddCoreConstants.EXCLUSION_FILTERS_KEY, (List<String>)null);
            if(exclusionFilters == null){
                ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();

            }
        } catch (CoreException e) {
            e.printStackTrace();
        }


	}
    public void setDefaultFilters(ILaunchConfigurationWorkingCopy configuration){
        try{
            String preferenceName = null;
            String launchTypeId = configuration.getType().getIdentifier();
//            if(launchTypeId.equals()
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if(isDirty()){
            boolean selected = enableOddButton.getSelection();
            if(selected){
                configuration.addModes(oddModes);
            }else {
                configuration.removeModes(oddModes);
            }
            List<String> filters = new ArrayList<>();
            filters.addAll(Arrays.asList(filtersList.getItems()));
//            configuration.setAttribute();
        }

	}

	@Override
	public String getName() {
		return OBJECTDIAGRAM_TAB_NAME;
	}

}
