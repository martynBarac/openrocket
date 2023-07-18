package net.sf.openrocket.startup;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.openrocket.database.Databases;
import net.sf.openrocket.material.Material;
import net.sf.openrocket.models.atmosphere.AtmosphericModel;
import net.sf.openrocket.models.atmosphere.ExtendedISAModel;
import net.sf.openrocket.preset.ComponentPreset;
import net.sf.openrocket.rocketcomponent.BodyComponent;
import net.sf.openrocket.rocketcomponent.FinSet;
import net.sf.openrocket.rocketcomponent.InternalComponent;
import net.sf.openrocket.rocketcomponent.LaunchLug;
import net.sf.openrocket.rocketcomponent.MassObject;
import net.sf.openrocket.rocketcomponent.RailButton;
import net.sf.openrocket.rocketcomponent.RecoveryDevice;
import net.sf.openrocket.rocketcomponent.RocketComponent;
import net.sf.openrocket.rocketcomponent.TubeFinSet;
import net.sf.openrocket.simulation.RK4SimulationStepper;
import net.sf.openrocket.util.BugException;
import net.sf.openrocket.util.BuildProperties;
import net.sf.openrocket.util.ChangeSource;
import net.sf.openrocket.util.Color;
import net.sf.openrocket.util.GeodeticComputationStrategy;
import net.sf.openrocket.util.LineStyle;
import net.sf.openrocket.util.MathUtil;
import net.sf.openrocket.util.StateChangeListener;
import net.sf.openrocket.util.UniqueID;

public abstract class Preferences implements ChangeSource {
	
	/*
	 * Well known string keys to preferences.
	 * There are other strings out there in the source as well.
	 */
	public static final String BODY_COMPONENT_INSERT_POSITION_KEY = "BodyComponentInsertPosition";
	public static final String STAGE_INSERT_POSITION_KEY = "StageInsertPosition";
	public static final String USER_THRUST_CURVES_KEY = "UserThrustCurves";
	
	public static final String DEFAULT_MACH_NUMBER = "DefaultMachNumber";

	// Preferences related to units
	public static final String DISPLAY_SECONDARY_STABILITY = "DisplaySecondaryStability";

	// Preferences related to data export
	public static final String EXPORT_FIELD_SEPARATOR = "ExportFieldSeparator";
	public static final String EXPORT_DECIMAL_PLACES = "ExportDecimalPlaces";
	public static final String EXPORT_EXPONENTIAL_NOTATION = "ExportExponentialNotation";
	public static final String EXPORT_SIMULATION_COMMENT = "ExportSimulationComment";
	public static final String EXPORT_FIELD_NAME_COMMENT = "ExportFieldDescriptionComment";
	public static final String EXPORT_EVENT_COMMENTS = "ExportEventComments";
	public static final String EXPORT_COMMENT_CHARACTER = "ExportCommentCharacter";
	public static final String USER_LOCAL = "locale";
	public static final String DEFAULT_DIRECTORY = "defaultDirectory";

	public static final String PLOT_SHOW_POINTS = "ShowPlotPoints";

	private static final String IGNORE_WELCOME = "IgnoreWelcome";

	private static final String CHECK_UPDATES = "CheckUpdates";

	private static final String IGNORE_UPDATE_VERSIONS = "IgnoreUpdateVersions";
	private static final String CHECK_BETA_UPDATES = "CheckBetaUpdates";
	
	public static final String MOTOR_DIAMETER_FILTER = "MotorDiameterMatch";
	public static final String MOTOR_HIDE_SIMILAR = "MotorHideSimilar";
	public static final String MOTOR_HIDE_UNAVAILABLE = "MotorHideUnavailable";

	public static final String MOTOR_NAME_COLUMN = "MotorNameColumn";

	public static final String MATCH_FORE_DIAMETER = "MatchForeDiameter";
	public static final String MATCH_AFT_DIAMETER = "MatchAftDiameter";
	
	// Node names
	public static final String PREFERRED_THRUST_CURVE_MOTOR_NODE = "PreferredThrustCurveMotors";
	private static final String AUTO_OPEN_LAST_DESIGN = "AutoOpenLastDesign";
	private static final String OPEN_LEFTMOST_DESIGN_TAB = "OpenLeftmostDesignTab";
	private static final String SHOW_DISCARD_CONFIRMATION = "IgnoreDiscardEditingWarning";
	private static final String SHOW_SAVE_ROCKET_INFO = "ShowSaveRocketInfo";
	private static final String SHOW_DISCARD_SIMULATION_CONFIRMATION = "IgnoreDiscardSimulationEditingWarning";
	private static final String SHOW_DISCARD_PREFERENCES_CONFIRMATION = "IgnoreDiscardPreferencesWarning";
	public static final String MARKER_STYLE_ICON = "MarkerStyleIcon";
	private static final String SHOW_MARKERS = "ShowMarkers";
	private static final String SHOW_RASAERO_FORMAT_WARNING = "ShowRASAeroFormatWarning";
	private static final String SHOW_ROCKSIM_FORMAT_WARNING = "ShowRockSimFormatWarning";
	private static final String EXPORT_USER_DIRECTORIES = "ExportUserDirectories";
	private static final String EXPORT_WINDOW_INFORMATION = "ExportWindowInformation";
	
	//Preferences related to 3D graphics
	public static final String OPENGL_ENABLED = "OpenGLIsEnabled";
	public static final String OPENGL_ENABLE_AA = "OpenGLAntialiasingIsEnabled";
	public static final String OPENGL_USE_FBO = "OpenGLUseFBO";
	
	public static final String ROCKET_INFO_FONT_SIZE = "RocketInfoFontSize";
	
	//Preferences Related to Simulations
	
	public static final String CONFIRM_DELETE_SIMULATION = "ConfirmDeleteSimulation";
	public static final String AUTO_RUN_SIMULATIONS = "AutoRunSimulations";
	public static final String LAUNCH_ROD_LENGTH = "LaunchRodLength";
	public static final String LAUNCH_INTO_WIND = "LaunchIntoWind";
	public static final String LAUNCH_ROD_ANGLE = "LaunchRodAngle";
	public static final String LAUNCH_ROD_DIRECTION = "LaunchRodDirection";
	public static final String WIND_DIRECTION = "WindDirection";
	public static final String WIND_AVERAGE = "WindAverage";
	public static final String WIND_TURBULENCE = "WindTurbulence";
	public static final String LAUNCH_ALTITUDE = "LaunchAltitude";
	public static final String LAUNCH_LATITUDE = "LaunchLatitude";
	public static final String LAUNCH_LONGITUDE = "LaunchLongitude";
	public static final String LAUNCH_TEMPERATURE = "LaunchTemperature";
	public static final String LAUNCH_PRESSURE = "LaunchPressure";
	public static final String LAUNCH_USE_ISA = "LaunchUseISA";
	public static final String SIMULATION_TIME_STEP = "SimulationTimeStep";
	public static final String GEODETIC_COMPUTATION = "GeodeticComputationStrategy";
	
	
	private static final AtmosphericModel ISA_ATMOSPHERIC_MODEL = new ExtendedISAModel();
	
	/*
	 * ******************************************************************************************
	 *
	 * Abstract methods which must be implemented by any derived class.
	 */
	public abstract boolean getBoolean(String key, boolean defaultValue);
	
	public abstract void putBoolean(String key, boolean value);
	
	public abstract int getInt(String key, int defaultValue);
	
	public abstract void putInt(String key, int value);
	
	public abstract double getDouble(String key, double defaultValue);
	
	public abstract void putDouble(String key, double value);
	
	public abstract String getString(String key, String defaultValue);
	
	public abstract void putString(String key, String value);
	
	/**
	 * Directory represents a way to collect multiple keys together.  Implementors may
	 * choose to concatenate the directory with the key using some special character.
	 * @param directory
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public abstract String getString(String directory, String key, String defaultValue);
	
	public abstract void putString(String directory, String key, String value);
	
	public abstract java.util.prefs.Preferences getNode(String nodeName);

	/*
	 * Welcome dialog
	 */

	/**
	 * Sets to ignore opening the welcome dialog for the supplied OpenRocket build version.
	 * @param version build version to ignore opening the welcome dialog for (e.g. "22.02")
	 * @param ignore true to ignore, false to show the welcome dialog
	 */
	public final void setIgnoreWelcome(String version, boolean ignore) {
		this.putBoolean(IGNORE_WELCOME + "_" + version, ignore);
	}

	/**
	 * Returns whether to ignore opening the welcome dialog for the supplied OpenRocket build version.
	 * @param version build version (e.g. "22.02")
	 * @return true if no welcome dialog should be opened for the supplied version
	 */
	public final boolean getIgnoreWelcome(String version) {
		return this.getBoolean(IGNORE_WELCOME + "_" + version, false);
	}

	/*
	 * Software updater
	 */
	public final boolean getCheckUpdates() {
		return this.getBoolean(CHECK_UPDATES, BuildProperties.getDefaultCheckUpdates());
	}
	
	public final void setCheckUpdates(boolean check) {
		this.putBoolean(CHECK_UPDATES, check);
	}

	public final List<String> getIgnoreUpdateVersions() {
		return List.of(this.getString(IGNORE_UPDATE_VERSIONS, "").split("\n"));
	}

	public final void setIgnoreUpdateVersions(List<String> versions) {
		this.putString(IGNORE_UPDATE_VERSIONS, String.join("\n", versions));
	}

	public final boolean getCheckBetaUpdates() {
		return this.getBoolean(CHECK_BETA_UPDATES, BuildProperties.getDefaultCheckBetaUpdates());
	}

	public final void setCheckBetaUpdates(boolean check) {
		this.putBoolean(CHECK_BETA_UPDATES, check);
	}


	/*
	 * *********************** Unit Preferences *******************************************
	 */

	/**
	 * Return whether to display a secondary stability unit in the rocket design view.
	 * @return true if the secondary unit should be displayed, false if not.
	 */
	public final boolean isDisplaySecondaryStability() {
		return this.getBoolean(DISPLAY_SECONDARY_STABILITY, true);
	}

	/**
	 * Set whether to display a secondary stability unit in the rocket design view.
	 * @param check if true, display the secondary unit, if false not.
	 */
	public final void setDisplaySecondaryStability(boolean check) {
		this.putBoolean(DISPLAY_SECONDARY_STABILITY, check);
	}


	/*
	 * ******************************************************************************************
	 */
	
	public final boolean getConfirmSimDeletion() {
		return this.getBoolean(CONFIRM_DELETE_SIMULATION, true);
	}
	
	public final void setConfirmSimDeletion(boolean check) {
		this.putBoolean(CONFIRM_DELETE_SIMULATION, check);
	}
	
	public final boolean getAutoRunSimulations() {
		return this.getBoolean(AUTO_RUN_SIMULATIONS, false);
	}
	
	public final void setAutoRunSimulations(boolean check) {
		this.putBoolean(AUTO_RUN_SIMULATIONS, check);
	}
	
	public final boolean getLaunchIntoWind() {
		return this.getBoolean(LAUNCH_INTO_WIND, false);
	}
	
	public final void setLaunchIntoWind(boolean check) {
		this.putBoolean(LAUNCH_INTO_WIND, check);
	}

	public final boolean getShowRASAeroFormatWarning() {
		return this.getBoolean(SHOW_RASAERO_FORMAT_WARNING, true);
	}

	public final void setShowRASAeroFormatWarning(boolean check) {
		this.putBoolean(SHOW_RASAERO_FORMAT_WARNING, check);
	}
	
	public final boolean getShowRockSimFormatWarning() {
		return this.getBoolean(SHOW_ROCKSIM_FORMAT_WARNING, true);
	}
	
	public final void setShowRockSimFormatWarning(boolean check) {
		this.putBoolean(SHOW_ROCKSIM_FORMAT_WARNING, check);
	}

	public final boolean getExportUserDirectories() {
		return this.getBoolean(EXPORT_USER_DIRECTORIES, false);
	}

	public final void setExportUserDirectories(boolean check) {
		this.putBoolean(EXPORT_USER_DIRECTORIES, check);
	}

	public final boolean getExportWindowInformation() {
		return this.getBoolean(EXPORT_WINDOW_INFORMATION, false);
	}

	public final void setExportWindowInformation(boolean check) {
		this.putBoolean(EXPORT_WINDOW_INFORMATION, check);
	}

	public final double getDefaultMach() {
		return Application.getPreferences().getChoice(Preferences.DEFAULT_MACH_NUMBER, 0.9, 0.3);
	}
	
	public final void setDefaultMach(double dfn) {
		double oldDFN = Application.getPreferences().getChoice(Preferences.DEFAULT_MACH_NUMBER, 0.9, 0.3);
		
		if (MathUtil.equals(oldDFN, dfn))
			return;
		this.putDouble(Preferences.DEFAULT_MACH_NUMBER, dfn);
		fireChangeEvent();
	}
	
	public final double getWindTurbulenceIntensity() {
		return Application.getPreferences().getChoice(Preferences.WIND_TURBULENCE, 0.9, 0.1);
	}
	
	public final void setWindTurbulenceIntensity(double wti) {
		double oldWTI = Application.getPreferences().getChoice(Preferences.WIND_TURBULENCE, 0.9, 0.3);
		
		if (MathUtil.equals(oldWTI, wti))
			return;
		this.putDouble(Preferences.WIND_TURBULENCE, wti);
		fireChangeEvent();
	}
	
	public double getLaunchRodLength() {
		return this.getDouble(LAUNCH_ROD_LENGTH, 1);
	}
	
	public void setLaunchRodLength(double launchRodLength) {
		if (MathUtil.equals(this.getDouble(LAUNCH_ROD_LENGTH, 1), launchRodLength))
			return;
		this.putDouble(LAUNCH_ROD_LENGTH, launchRodLength);
		fireChangeEvent();
	}
	
	
	public double getLaunchRodAngle() {
		return this.getDouble(LAUNCH_ROD_ANGLE, 0);
	}
	
	public void setLaunchRodAngle(double launchRodAngle) {
		launchRodAngle = MathUtil.clamp(launchRodAngle, -Math.PI / 6.0, Math.PI / 6.0);
		if (MathUtil.equals(this.getDouble(LAUNCH_ROD_ANGLE, 0), launchRodAngle))
			return;
		this.putDouble(LAUNCH_ROD_ANGLE, launchRodAngle);
		fireChangeEvent();
	}
	
	
	public double getLaunchRodDirection() {
		if (this.getBoolean(LAUNCH_INTO_WIND, true)) {
			this.setLaunchRodDirection(this.getDouble(WIND_DIRECTION, Math.PI / 2));
		}
		return this.getDouble(WIND_DIRECTION, Math.PI / 2);
	}
	
	public void setLaunchRodDirection(double launchRodDirection) {
		launchRodDirection = MathUtil.reduce2Pi(launchRodDirection);
		if (MathUtil.equals(this.getDouble(LAUNCH_ROD_DIRECTION, Math.PI / 2.0), launchRodDirection))
			return;
		this.putDouble(LAUNCH_ROD_DIRECTION, launchRodDirection);
		fireChangeEvent();
	}
	
	
	
	public double getWindSpeedAverage() {
		return this.getDouble(WIND_AVERAGE, 2);
	}
	
	public void setWindSpeedAverage(double windAverage) {
		if (MathUtil.equals(this.getDouble(WIND_AVERAGE, 2), windAverage))
			return;
		this.putDouble(WIND_AVERAGE, MathUtil.max(windAverage, 0));
		fireChangeEvent();
	}
	
	
	public double getWindSpeedDeviation() {
		return this.getDouble(WIND_AVERAGE, 2) * this.getDouble(WIND_TURBULENCE, .1);
	}
	
	public void setWindSpeedDeviation(double windDeviation) {
		double windAverage = this.getDouble(WIND_DIRECTION, 2);
		if (windAverage < 0.1) {
			windAverage = 0.1;
		}
		setWindTurbulenceIntensity(windDeviation / windAverage);
	}
	
	public void setWindDirection(double direction) {
		direction = MathUtil.reduce2Pi(direction);
		if (this.getBoolean(LAUNCH_INTO_WIND, true)) {
			this.setLaunchRodDirection(direction);
		}
		if (MathUtil.equals(this.getDouble(WIND_DIRECTION, Math.PI / 2), direction))
			return;
		this.putDouble(WIND_DIRECTION, direction);
		fireChangeEvent();
		
	}
	
	public double getWindDirection() {
		return this.getDouble(WIND_DIRECTION, Math.PI / 2);
		
	}
	
	public double getLaunchAltitude() {
		return this.getDouble(LAUNCH_ALTITUDE, 0);
	}
	
	public void setLaunchAltitude(double altitude) {
		if (MathUtil.equals(this.getDouble(LAUNCH_ALTITUDE, 0), altitude))
			return;
		this.putDouble(LAUNCH_ALTITUDE, altitude);

		// Update the launch temperature and pressure if using ISA
		if (getISAAtmosphere()) {
			setLaunchTemperature(ISA_ATMOSPHERIC_MODEL.getConditions(getLaunchAltitude()).getTemperature());
			setLaunchPressure(ISA_ATMOSPHERIC_MODEL.getConditions(getLaunchAltitude()).getPressure());
		}

		fireChangeEvent();
	}
	
	
	public double getLaunchLatitude() {
		return this.getDouble(LAUNCH_LATITUDE, 28.61);
	}
	
	public void setLaunchLatitude(double launchLatitude) {
		launchLatitude = MathUtil.clamp(launchLatitude, -90, 90);
		if (MathUtil.equals(this.getDouble(LAUNCH_LATITUDE, 28.61), launchLatitude))
			return;
		this.putDouble(LAUNCH_LATITUDE, launchLatitude);
		fireChangeEvent();
	}
	
	public double getLaunchLongitude() {
		return this.getDouble(LAUNCH_LONGITUDE, -80.60);
	}
	
	public void setLaunchLongitude(double launchLongitude) {
		launchLongitude = MathUtil.clamp(launchLongitude, -180, 180);
		if (MathUtil.equals(this.getDouble(LAUNCH_LONGITUDE, -80.60), launchLongitude))
			return;
		this.putDouble(LAUNCH_LONGITUDE, launchLongitude);
		fireChangeEvent();
	}
	
	/*	
		public GeodeticComputationStrategy getGeodeticComputation() {
			return geodeticComputation;
		}
		
		public void setGeodeticComputation(GeodeticComputationStrategy geodeticComputation) {
			if (this.geodeticComputation == geodeticComputation)
				return;
			if (geodeticComputation == null) {
				throw new IllegalArgumentException("strategy cannot be null");
			}
			this.geodeticComputation = geodeticComputation;
			fireChangeEvent();
		}
		
		
		public boolean isISAAtmosphere() {
			return useISA;
		}
		
		public void setISAAtmosphere(boolean isa) {
			if (isa == useISA)
				return;
			useISA = isa;
			fireChangeEvent();
		}
		*/
	
	public double getLaunchTemperature() {
		return this.getDouble(LAUNCH_TEMPERATURE, ExtendedISAModel.STANDARD_TEMPERATURE);
	}
	
	
	
	public void setLaunchTemperature(double launchTemperature) {
		if (MathUtil.equals(this.getDouble(LAUNCH_TEMPERATURE, ExtendedISAModel.STANDARD_TEMPERATURE), launchTemperature))
			return;
		this.putDouble(LAUNCH_TEMPERATURE, launchTemperature);
		fireChangeEvent();
	}
	
	
	
	public double getLaunchPressure() {
		return this.getDouble(LAUNCH_PRESSURE, ExtendedISAModel.STANDARD_PRESSURE);
	}
	
	
	
	public void setLaunchPressure(double launchPressure) {
		if (MathUtil.equals(this.getDouble(LAUNCH_PRESSURE, ExtendedISAModel.STANDARD_PRESSURE), launchPressure))
			return;
		this.putDouble(LAUNCH_PRESSURE, launchPressure);
		fireChangeEvent();
	}
	
	
	public boolean getISAAtmosphere() {
		return this.getBoolean(LAUNCH_USE_ISA, true);
	}
	
	public void setISAAtmosphere(boolean isa) {
		if (this.getBoolean(LAUNCH_USE_ISA, true) == isa) {
			return;
		}
		this.putBoolean(LAUNCH_USE_ISA, isa);

		// Update the launch temperature and pressure
		if (isa) {
			setLaunchTemperature(ISA_ATMOSPHERIC_MODEL.getConditions(getLaunchAltitude()).getTemperature());
			setLaunchPressure(ISA_ATMOSPHERIC_MODEL.getConditions(getLaunchAltitude()).getPressure());
		}

		fireChangeEvent();
	}
	
	/**
	 * Returns an atmospheric model corresponding to the launch conditions.  The
	 * atmospheric models may be shared between different calls.
	 * 
	 * @return	an AtmosphericModel object.
	 */
	public AtmosphericModel getAtmosphericModel() {
		if (this.getBoolean(LAUNCH_USE_ISA, true)) {
			return ISA_ATMOSPHERIC_MODEL;
		}
		return new ExtendedISAModel(getLaunchAltitude(), this.getDouble(LAUNCH_TEMPERATURE, ExtendedISAModel.STANDARD_TEMPERATURE),
				this.getDouble(LAUNCH_PRESSURE, ExtendedISAModel.STANDARD_PRESSURE));
	}
	
	public GeodeticComputationStrategy getGeodeticComputation() {
		return this.getEnum(GEODETIC_COMPUTATION, GeodeticComputationStrategy.SPHERICAL);
	}
	
	public void setGeodeticComputation(GeodeticComputationStrategy gcs) {
		this.putEnum(GEODETIC_COMPUTATION, gcs);
	}
	
	public double getTimeStep() {
		return this.getDouble(Preferences.SIMULATION_TIME_STEP, RK4SimulationStepper.RECOMMENDED_TIME_STEP);
	}
	
	public void setTimeStep(double timeStep) {
		if (MathUtil.equals(this.getDouble(SIMULATION_TIME_STEP, RK4SimulationStepper.RECOMMENDED_TIME_STEP), timeStep))
			return;
		this.putDouble(SIMULATION_TIME_STEP, timeStep);
		fireChangeEvent();
	}
	
	
	public final float getRocketInfoFontSize() {
		return (float) (11.0 + 3 * Application.getPreferences().getChoice(Preferences.ROCKET_INFO_FONT_SIZE, 2, 0));
	}
	
	/**
	 * Enable/Disable the auto-opening of the last edited design file on startup.
	 */
	public final void setAutoOpenLastDesignOnStartup(boolean enabled) {
		this.putBoolean(AUTO_OPEN_LAST_DESIGN, enabled);
	}
	
	/**
	 * Answer if the auto-opening of the last edited design file on startup is enabled.
	 *
	 * @return true if the application should automatically open the last edited design file on startup.
	 */
	public final boolean isAutoOpenLastDesignOnStartupEnabled() {
		return this.getBoolean(AUTO_OPEN_LAST_DESIGN, false);
	}

	/**
	 * Enable/Disable the opening the leftmost tab on the component design panel, or using the tab that was opened last time.
	 */
	public final void setAlwaysOpenLeftmostTab(boolean enabled) {
		this.putBoolean(OPEN_LEFTMOST_DESIGN_TAB, enabled);
	}

	/**
	 * Answer if a confirmation dialog should be shown when canceling a component config operation.
	 *
	 * @return true if the confirmation dialog should be shown.
	 */
	public final boolean isShowDiscardConfirmation() {
		return this.getBoolean(SHOW_DISCARD_CONFIRMATION, true);
	}

	/**
	 * Enable/Disable showing a confirmation warning when canceling a component config operation.
	 */
	public final void setShowDiscardConfirmation(boolean enabled) {
		this.putBoolean(SHOW_DISCARD_CONFIRMATION, enabled);
	}

	/**
	 * Returns whether a 'save rocket information' dialog should be shown after saving a new design file.
	 * @return true if the 'save rocket information' dialog should be shown.
	 */
	public final boolean isShowSaveRocketInfo() {
		return this.getBoolean(SHOW_SAVE_ROCKET_INFO, true);
	}

	/**
	 * Enable/Disable showing a 'save rocket information' dialog after saving a new design file.
	 * @return true if the 'save rocket information' dialog should be shown.
	 */
	public final void setShowSaveRocketInfo(boolean enabled) {
		this.putBoolean(SHOW_SAVE_ROCKET_INFO, enabled);
	}
	/**
	 * Answer if a confirmation dialog should be shown when canceling a simulation config operation.
	 *
	 * @return true if the confirmation dialog should be shown.
	 */
	public final boolean isShowDiscardSimulationConfirmation() {
		return this.getBoolean(SHOW_DISCARD_SIMULATION_CONFIRMATION, true);
	}

	/**
	 * Enable/Disable showing a confirmation warning when canceling a simulation config operation.
	 */
	public final void setShowDiscardSimulationConfirmation(boolean enabled) {
		this.putBoolean(SHOW_DISCARD_SIMULATION_CONFIRMATION, enabled);
	}

	/**
	 * Answer if a confirmation dialog should be shown when canceling preferences changes.
	 *
	 * @return true if the confirmation dialog should be shown.
	 */
	public final boolean isShowDiscardPreferencesConfirmation() {
		return this.getBoolean(SHOW_DISCARD_PREFERENCES_CONFIRMATION, true);
	}

	/**
	 * Enable/Disable showing a confirmation warning when canceling preferences changes.
	 */
	public final void setShowDiscardPreferencesConfirmation(boolean enabled) {
		this.putBoolean(SHOW_DISCARD_PREFERENCES_CONFIRMATION, enabled);
	}

	/**
	 * Answer if the always open leftmost tab is enabled.
	 *
	 * @return true if the application should always open the leftmost tab in the component design panel.
	 */
	public final boolean isAlwaysOpenLeftmostTab() {
		return this.getBoolean(OPEN_LEFTMOST_DESIGN_TAB, false);
	}

	/**
	 * Set whether pod set/booster markers should only be displayed when the pod set/booster is selected.
	 * @param enabled 	true if pod set/booster markers should only be displayed when the pod set/booster is selected,
	 * 					false if they should be displayed permanently.
	 */
	public final void setShowMarkers(boolean enabled) {
		this.putBoolean(SHOW_MARKERS, enabled);
	}

	/**
	 * Answer if pod set/booster markers should only be displayed when the pod set/booster is selected
	 *
	 * @return 	true if pod set/booster markers should only be displayed when the pod set/booster is selected,
	 * 			false if they should be displayed permanently.
	 */
	public final boolean isShowMarkers() {
		return this.getBoolean(SHOW_MARKERS, false);
	}

	/**
	 * Set whether the component preset chooser dialog should filter by fore diameter when the window is opened.
	 * @param enabled 	true if the fore diameter filter should be enabled,
	 * 					false if it should be disabled.
	 */
	public final void setMatchForeDiameter(boolean enabled) {
		this.putBoolean(MATCH_FORE_DIAMETER, enabled);
	}

	/**
	 * Answer if the component preset chooser dialog should filter by fore diameter when the window is opened.
	 *
	 * @return 	true if the fore diameter filter should be enabled,
	 * 			false if it should be disabled.
	 */
	public final boolean isMatchForeDiameter() {
		return this.getBoolean(MATCH_FORE_DIAMETER, true);
	}

	/**
	 * Set whether the component preset chooser dialog should filter by aft diameter when the window is opened.
	 * @param enabled 	true if the aft diameter filter should be enabled,
	 * 					false if it should be disabled.
	 */
	public final void setMatchAftDiameter(boolean enabled) {
		this.putBoolean(MATCH_AFT_DIAMETER, enabled);
	}

	/**
	 * Answer if the component preset chooser dialog should filter by aft diameter when the window is opened.
	 *
	 * @return 	true if the aft diameter filter should be enabled,
	 * 			false if it should be disabled.
	 */
	public final boolean isMatchAftDiameter() {
		return this.getBoolean(MATCH_AFT_DIAMETER, true);
	}

	/**
	 * Check whether to display the common name (false), or designation (true) in the motor selection table "Name" column
	 * @return true to display designation, false to display common name
	 */
	public boolean getMotorNameColumn() {
		return getBoolean(net.sf.openrocket.startup.Preferences.MOTOR_NAME_COLUMN, true);
	}

	/**
	 * Set whether to display the common name, or designation in the motor selection table "Name" column
	 * @param value if true, display designation, if false, display common name
	 */
	public void setMotorNameColumn(boolean value) {
		putBoolean(net.sf.openrocket.startup.Preferences.MOTOR_NAME_COLUMN, value);
	}

	/**
	 * Return the OpenRocket unique ID.
	 *
	 * @return	a random ID string that stays constant between OpenRocket executions
	 */
	public final String getUniqueID() {
		String id = this.getString("id", null);
		if (id == null) {
			id = UniqueID.uuid();
			this.putString("id", id);
		}
		return id;
	}
	
	/**
	 * Returns a limited-range integer value from the preferences.  If the value
	 * in the preferences is negative or greater than max, then the default value
	 * is returned.
	 *
	 * @param key  The preference to retrieve.
	 * @param max  Maximum allowed value for the choice.
	 * @param def  Default value.
	 * @return   The preference value.
	 */
	public final int getChoice(String key, int max, int def) {
		int v = this.getInt(key, def);
		if ((v < 0) || (v > max))
			return def;
		return v;
	}
	
	/**
	 * Returns a limited-range double value from the preferences.  If the value
	 * in the preferences is negative or greater than max, then the default value
	 * is returned.
	 *
	 * @param key  The preference to retrieve.
	 * @param max  Maximum allowed value for the choice.
	 * @param def  Default value.
	 * @return   The preference value.
	 */
	public final double getChoice(String key, double max, double def) {
		double v = this.getDouble(key, def);
		if ((v < 0) || (v > max))
			return def;
		return v;
	}
	
	
	/**
	 * Helper method that puts an integer choice value into the preferences.
	 *
	 * @param key     the preference key.
	 * @param value   the value to store.
	 */
	public final void putChoice(String key, int value) {
		this.putInt(key, value);
	}
	
	/**
	 * Retrieve an enum value from the user preferences.
	 *
	 * @param <T>	the enum type
	 * @param key	the key
	 * @param def	the default value, cannot be null
	 * @return		the value in the preferences, or the default value
	 */
	public final <T extends Enum<T>> T getEnum(String key, T def) {
		if (def == null) {
			throw new BugException("Default value cannot be null");
		}
		
		String value = getString(key, null);
		if (value == null) {
			return def;
		}
		
		try {
			return Enum.valueOf(def.getDeclaringClass(), value);
		} catch (IllegalArgumentException e) {
			return def;
		}
	}
	
	/**
	 * Store an enum value to the user preferences.
	 *
	 * @param key		the key
	 * @param value		the value to store, or null to remove the value
	 */
	public final void putEnum(String key, Enum<?> value) {
		if (value == null) {
			putString(key, null);
		} else {
			putString(key, value.name());
		}
	}
	
	public Color getDefaultColor(Class<? extends RocketComponent> c) {
		String color = get("componentColors", c, StaticFieldHolder.DEFAULT_COLORS);
		if (color == null)
			return Color.BLACK;
			
		Color clr = parseColor(color);
		if (clr != null) {
			return clr;
		} else {
			return Color.BLACK;
		}
	}
	
	public final void setDefaultColor(Class<? extends RocketComponent> c, Color color) {
		if (color == null)
			return;
		putString("componentColors", c.getSimpleName(), stringifyColor(color));
	}
	
	
	/**
	 * Retrieve a Line style for the given component.
	 * @param c
	 * @return
	 */
	public final LineStyle getDefaultLineStyle(Class<? extends RocketComponent> c) {
		String value = get("componentStyle", c, StaticFieldHolder.DEFAULT_LINE_STYLES);
		try {
			return LineStyle.valueOf(value);
		} catch (Exception e) {
			return LineStyle.SOLID;
		}
	}
	
	/**
	 * Set a default line style for the given component.
	 * @param c
	 * @param style
	 */
	public final void setDefaultLineStyle(Class<? extends RocketComponent> c,
			LineStyle style) {
		if (style == null)
			return;
		putString("componentStyle", c.getSimpleName(), style.name());
	}
	
	/**
	 * Get the default material type for the given component.
	 * @param componentClass
	 * @param type the Material.Type to return.
	 * @return
	 */
	public Material getDefaultComponentMaterial(
			Class<? extends RocketComponent> componentClass,
			Material.Type type) {
			
		String material = get("componentMaterials", componentClass, null);
		if (material != null) {
			try {
				Material m = Material.fromStorableString(material, false);
				if (m.getType() == type)
					return m;
			} catch (IllegalArgumentException ignore) {
			}
		}
		
		switch (type) {
		case LINE:
			return StaticFieldHolder.DEFAULT_LINE_MATERIAL;
		case SURFACE:
			return StaticFieldHolder.DEFAULT_SURFACE_MATERIAL;
		case BULK:
			return StaticFieldHolder.DEFAULT_BULK_MATERIAL;
		}
		throw new IllegalArgumentException("Unknown material type: " + type);
	}
	
	/**
	 * Set the default material for a component type.
	 * @param componentClass
	 * @param material
	 */
	public void setDefaultComponentMaterial(
			Class<? extends RocketComponent> componentClass, Material material) {
			
		putString("componentMaterials", componentClass.getSimpleName(),
				material == null ? null : material.toStorableString());
	}
	
	/**
	 * get a net.sf.openrocket.util.Color object for the given key.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public final Color getColor(String key, Color defaultValue) {
		Color c = parseColor(getString(key, null));
		if (c == null) {
			return defaultValue;
		}
		return c;
	}
	
	/**
	 * set a net.sf.openrocket.util.Color preference value for the given key.
	 * @param key
	 * @param value
	 */
	public final void putColor(String key, Color value) {
		putString(key, stringifyColor(value));
	}
	
	/**
	 * Helper function to convert a string representation into a net.sf.openrocket.util.Color object.
	 * @param color
	 * @return
	 */
	protected static Color parseColor(String color) {
		if (color == null) {
			return null;
		}
		
		String[] rgb = color.split(",");
		if (rgb.length == 3) {
			try {
				int red = MathUtil.clamp(Integer.parseInt(rgb[0]), 0, 255);
				int green = MathUtil.clamp(Integer.parseInt(rgb[1]), 0, 255);
				int blue = MathUtil.clamp(Integer.parseInt(rgb[2]), 0, 255);
				return new Color(red, green, blue);
			} catch (NumberFormatException ignore) {
			}
		}
		return null;
	}
	
	/**
	 * Helper function to convert a net.sf.openrocket.util.Color object into a
	 * String before storing in a preference.
	 * @param color
	 * @return
	 */
	protected static String stringifyColor(Color color) {
		String string = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
		return string;
	}
	
	/**
	 * Special helper function which allows for a map of default values.
	 *
	 * First getString(directory,componentClass.getSimpleName(), null) is invoked,
	 * if the returned value is null, the defaultMap is consulted for a value.
	 *
	 * @param directory
	 * @param componentClass
	 * @param defaultMap
	 * @return
	 */
	protected String get(String directory,
			Class<? extends RocketComponent> componentClass,
			Map<Class<?>, String> defaultMap) {
			
		// Search preferences
		Class<?> c = componentClass;
		while (c != null && RocketComponent.class.isAssignableFrom(c)) {
			String value = this.getString(directory, c.getSimpleName(), null);
			if (value != null)
				return value;
			c = c.getSuperclass();
		}
		
		if (defaultMap == null)
			return null;
			
		// Search defaults
		c = componentClass;
		while (RocketComponent.class.isAssignableFrom(c)) {
			String value = defaultMap.get(c);
			if (value != null)
				return value;
			c = c.getSuperclass();
		}
		
		return null;
	}
	
	public abstract void addUserMaterial(Material m);
	
	public abstract Set<Material> getUserMaterials();
	
	public abstract void removeUserMaterial(Material m);
	
	public abstract void setComponentFavorite(ComponentPreset preset, ComponentPreset.Type type, boolean favorite);
	
	public abstract Set<String> getComponentFavorites(ComponentPreset.Type type);
	
	/*
	 * Within a holder class so they will load only when needed.
	 */
	private static class StaticFieldHolder {
		private static final Material DEFAULT_LINE_MATERIAL = Databases.findMaterial(Material.Type.LINE, "Elastic cord (round 2 mm, 1/16 in)");
		private static final Material DEFAULT_SURFACE_MATERIAL = Databases.findMaterial(Material.Type.SURFACE, "Ripstop nylon");
		private static final Material DEFAULT_BULK_MATERIAL = Databases.findMaterial(Material.Type.BULK, "Cardboard");
		/*
		 * Map of default line styles
		 */
		
		private static final HashMap<Class<?>, String> DEFAULT_LINE_STYLES = new HashMap<Class<?>, String>();
		
		static {
			DEFAULT_LINE_STYLES.put(RocketComponent.class, LineStyle.SOLID.name());
			DEFAULT_LINE_STYLES.put(MassObject.class, LineStyle.DASHED.name());
		}
		
		private static final HashMap<Class<?>, String> DEFAULT_COLORS = new HashMap<Class<?>, String>();
		
		static {
			DEFAULT_COLORS.put(BodyComponent.class, "0,0,240");
			DEFAULT_COLORS.put(TubeFinSet.class, "0,0,200");
			DEFAULT_COLORS.put(FinSet.class, "0,0,200");
			DEFAULT_COLORS.put(LaunchLug.class, "0,0,180");
			DEFAULT_COLORS.put(RailButton.class, "0,0,180");
			DEFAULT_COLORS.put(InternalComponent.class, "170,0,100");
			DEFAULT_COLORS.put(MassObject.class, "0,0,0");
			DEFAULT_COLORS.put(RecoveryDevice.class, "255,0,0");
		}
	}
	
	private final List<EventListener> listeners = new ArrayList<EventListener>();
	private final EventObject event = new EventObject(this);
	
	@Override
	public void addChangeListener(StateChangeListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeChangeListener(StateChangeListener listener) {
		listeners.remove(listener);
	}
	
	private void fireChangeEvent() {
		
		// Copy the list before iterating to prevent concurrent modification exceptions.
		EventListener[] list = listeners.toArray(new EventListener[0]);
		for (EventListener l : list) {
			if (l instanceof StateChangeListener) {
				((StateChangeListener) l).stateChanged(event);
			}
		}
	}
}
